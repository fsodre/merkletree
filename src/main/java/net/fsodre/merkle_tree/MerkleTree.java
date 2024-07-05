package net.fsodre.merkle_tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import net.fsodre.merkle_tree.hashers.MerkleHash;
import net.fsodre.merkle_tree.nodes.InternalNode;
import net.fsodre.merkle_tree.nodes.LeafNode;
import net.fsodre.merkle_tree.nodes.MerkleNode;

/**
 * Represents a Merkle tree.
 *
 * This implementation allows dynamic behavior: leaf nodes can be added,
 * modified and deleted. Deleted leaves (and leaves created with null value)
 * will represent empty spaces in the leaves list. We do that instead of
 * compressing the list because otherwise a huge chunk of the internal nodes
 * would need to be updated every time we compressed the list, as the parents of
 * many of the remaining leaves would change.
 *
 * Instead, we just indicate an empty spot using the `null` value and use those
 * spots upon adding new leaves in order to be mindful with memory utilization.
 *
 */
final public class MerkleTree implements Serializable {
    private static final long serialVersionUID = 1L;

    // All nodes in the tree. The bottom layer (leaf nodes) is represented by nodes[0].
    private final ArrayList<ArrayList<MerkleNode>> nodes;

    // Position of each leaf node in the bottom layer (nodes[0]).
    private final HashMap<String, Integer> leafPosition;

    // List of positions of currently empty leafs (initialized as null or deleted).
    private final LinkedList<Integer> emptyLeafIndexes;

    // Root node.
    private MerkleNode root;

    /**
     * Initializes an empty tree.
     */
    public MerkleTree() {
        root = null;
        nodes = new ArrayList<>();
        nodes.add(new ArrayList<>());
        leafPosition = new HashMap<>();
        emptyLeafIndexes = new LinkedList<>();
    }

    /**
     * Adds a new leaf node to the tree. If there's an empty leaf in an existing
     * spot, it'll take that spot and update the existing ancestors. Otherwise
     * it increases the list of leaves and adds new ancestors if necessary.
     *
     * Returns the position in the bottom layer where the node was added.
     */
    public int addLeaf(LeafNode leaf) {
        int index = emptyLeafIndexes.isEmpty() ? levelSize(0) : emptyLeafIndexes.removeFirst();

        if (leaf != null) {
            leafPosition.put(leaf.getHash().toString(), index);
        } else {
            emptyLeafIndexes.addLast(index);
        }

        if (index == levelSize(0)) {
            getLeaves().add(leaf);
            processCreationAt(/* level= */0, /* index= */ index);
        } else {
            getLeaves().set(index, leaf);
            updateInternalNode(1, getParentIndex(index));
        }

        return index;
    }

    /**
     * Updates the data of a leaf, given its current hash in the tree. It'll
     * also update the hashes of its ancestors.
     *
     * @throws AssertionError if there's no leaf with the provided hash.
     */
    public LeafNode updateLeaf(MerkleHash hash, LeafNode newLeaf) {
        int currentIndex = leafPosition.getOrDefault(hash.toString(), -1);
        assert currentIndex > -1 : "Updating a non-existing leaf node";
        leafPosition.remove(hash.toString());
        return updateLeafAt(currentIndex, newLeaf);
    }

    /**
     * Updates the data of a leaf, given its position in the leaves layer. It'll
     * also update the hashes of its ancestors.
     *
     * @throws AssertionError if an invalid index is provided.
     */
    public LeafNode updateLeafAt(int index, LeafNode newLeaf) {
        assert index < getLeaves().size() : "Trying to update a leaf in an invalid position";
        leafPosition.put(newLeaf.getHash().toString(), index);
        getLeaves().set(index, newLeaf);
        updateInternalNode(1, getParentIndex(index));
        return newLeaf;
    }

    /**
     * Removes a leaf given its current hash in the tree. It'll also update the
     * hashes of its ancestors.
     *
     * @throws AssertionError if there's no leaf with the provided hash.
     */
    public void removeLeaf(MerkleHash hash) {
        int currentIndex = leafPosition.getOrDefault(hash.toString(), -1);
        assert currentIndex > -1 : "Removing a non-existing leaf node";
        removeLeafAt(currentIndex);
    }

    /**
     * Removes a leaf given its position in the leaves layer. It'll also update
     * the hashes of its ancestors.
     *
     * @throws AssertionError if an invalid index is provided.
     */
    public void removeLeafAt(int index) {
        assert index < getLeaves().size() : "Trying to remove a leaf in an invalid position";

        leafPosition.remove(getLeaves().get(index).toString());
        getLeaves().set(index, null);
        emptyLeafIndexes.addLast(index);
        updateInternalNode(1, getParentIndex(index));
    }

    /**
     * Returns the root of the tree.
     */
    public MerkleNode getRoot() {
        return root;
    }

    /**
     * Builds and returns a proof that a leaf with hash `leafHash` is in the
     * tree. Returns null if the leaf isn't in the tree.
     */
    public ExistenceProof buildExistenceProof(MerkleHash leafHash) {
        ExistenceProof proof = new ExistenceProof();

        int currentIndex = leafPosition.getOrDefault(leafHash.toString(), -1);

        if (currentIndex == -1) {
            return null;
        }

        for (int level = 0; level < treeHeight() - 1; level++, currentIndex = getParentIndex(currentIndex)) {
            if (currentIndex % 2 == 0) {
                MerkleNode siblingNode = getNode(level, currentIndex + 1);
                proof.addRightSibling(siblingNode != null ? siblingNode.getHash() : null);
            } else {
                MerkleNode siblingNode = getNode(level, currentIndex - 1);
                proof.addLeftSibling(siblingNode != null ? siblingNode.getHash() : null);
            }
        }

        return proof;
    }

    /**
     * Adds a new internal node at a specific tree level.
     */
    private MerkleNode addInternalNode(int level) {
        MerkleNode[] children = getChildren(level, levelSize(level));
        MerkleNode newNode = InternalNode.from(children[0], children[1]);

        // This level still doesn't exist, which means we are increasing the tree height and adding a new root
        // to it.
        if (level == nodes.size()) {
            ArrayList<MerkleNode> newLevel = new ArrayList<>(List.of(newNode));
            nodes.add(newLevel);
            this.root = newNode;
            return newNode;
        }

        List<MerkleNode> levelNodes = nodes.get(level);
        levelNodes.add(newNode);
        processCreationAt(level, levelNodes.size() - 1);
        return newNode;
    }

    /**
     * Propagate changes to ancestors of a new node. Creates new ancestor when
     * necessary.
     */
    private void processCreationAt(int level, int index) {
        int parentLevel = level + 1;
        int parentIndex = getParentIndex(index);
        MerkleNode parent = getNode(parentLevel, parentIndex);
        // The new node doesn't have a parent node in the level above, so we propagate
        // its creation.
        if (parent == null) {
            addInternalNode(parentLevel);
        } else {
            updateInternalNode(parentLevel, parentIndex);
        }
    }

    private MerkleNode getNode(int level, int index) {
        if (level >= nodes.size()) {
            return null;
        }
        List<MerkleNode> levelNodes = nodes.get(level);
        if (index >= levelNodes.size()) {
            return null;
        }
        return levelNodes.get(index);
    }

    /**
     * Updates the hash of the current node based on the current state of its
     * children. If not the root, recursively call it for its parent.
     */
    private InternalNode updateInternalNode(int level, int index) {
        assert level > 0 : "Calling updateInternalNode on a leaf node";

        InternalNode node = (InternalNode) getNode(level, index);
        assert node != null;
        MerkleNode[] children = getChildren(level, index);

        node.update(children[0], children[1]);

        // If not root, update ancestors too.
        if (level < treeHeight() - 1) {
            updateInternalNode(level + 1, getParentIndex(index));
        }
        return node;
    }

    private MerkleNode getChild(int parentLevel, int parentIndex, int childRelativeIndex) {
        assert childRelativeIndex < 2 : "Trying to get child node beyond the second one";
        assert parentLevel > 0 : "Trying to get child of leaf node";
        int childLevel = parentLevel - 1;
        int childIndex = parentIndex * 2 + childRelativeIndex;

        if (childIndex >= levelSize(childLevel)) {
            return null;
        }

        return getNode(childLevel, childIndex);
    }

    private MerkleNode[] getChildren(int parentLevel, int parentIndex) {
        return new MerkleNode[]{
            getChild(parentLevel, parentIndex, 0),
            getChild(parentLevel, parentIndex, 1),};
    }

    private int treeHeight() {
        return nodes.size();
    }

    private int levelSize(int level) {
        if (level >= treeHeight()) {
            return 0;
        }
        return nodes.get(level).size();
    }

    private List<MerkleNode> getLeaves() {
        return nodes.get(0);
    }

    private int getParentIndex(int childIndex) {
        return childIndex / 2;
    }
}
