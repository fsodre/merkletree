package net.fsodre.merkle_tree.nodes;

import net.fsodre.merkle_tree.hashers.MerkleHash;

/**
 * Represents internal nodes in the Merkle Tree (that is, the ones that are not
 * leaves). Internal nodes can have between zero and 2 children and are
 * dynamically created as the number of leaf nodes grow, but they can become
 * childless if their leaf node children are deleted.
 *
 * The hash of an internal node is computed as follows ('+'' means
 * concatenation): - 2 children: H(left_child.hash() + right_child.hash()) - 1
 * child (either left or right): H(child.hash()) - No child: null
 */
public final class InternalNode implements MerkleNode {
    private static final long serialVersionUID = 1L;

    // The hash associated with this node.
    private MerkleHash hash;

    /**
     * Creates an internal node given two child nodes.
     */
    public static InternalNode from(MerkleNode leftChild, MerkleNode rightChild) {
        return new InternalNode(leftChild, rightChild);
    }

    /**
     * Creates an internal node given only one child (the left one).
     */
    public static InternalNode from(MerkleNode child) {
        return new InternalNode(child, null);
    }

    /**
     * Returns the MerkleHash associated with this node.
     */
    @Override
    public MerkleHash getHash() {
        return hash;
    }

    /**
     * Updates the current hash associated with this node based on the current
     * state of its children.
     */
    public void update(MerkleNode leftChild, MerkleNode rightChild) {
        hash = concatenateHashes(leftChild, rightChild);
    }

    private InternalNode(MerkleNode leftChild, MerkleNode rightChild) {
        hash = null;
        update(leftChild, rightChild);
    }

    private MerkleHash concatenateHashes(MerkleNode leftChild, MerkleNode rightChild) {
        if (leftChild == null && rightChild == null) {
            return null;
        }

        // If there's only one child, let it be called "leftChild" for implementation simplicity later.
        if (leftChild == null) {
            leftChild = rightChild;
            rightChild = null;
        }

        byte[] concatenatedHash = rightChild == null ? leftChild.getHash().toBytes() : leftChild.getHash().concat(rightChild.getHash());
        return MerkleHash.fromData(concatenatedHash);
    }

}
