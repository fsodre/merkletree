package net.fsodre.merkle_tree;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.fsodre.merkle_tree.hashers.HasherProvider;
import net.fsodre.merkle_tree.hashers.MerkleHash;
import net.fsodre.merkle_tree.nodes.LeafNode;
import net.fsodre.merkle_tree.utils.TestHasher;
import static net.fsodre.merkle_tree.utils.TestHasher.paddedHash;

public class ExistenceProofTest {

    @BeforeAll
    public static void setUpSuite() {
        HasherProvider.initialize(new TestHasher());
    }

    @Test
    public void testSingleLeafInTree() throws Exception {
        ExistenceProof proof = new ExistenceProof();
        LeafNode node = LeafNode.fromData(new byte[]{0xa});

        proof.addRightSibling(null);

        assertTrue(proof.validate(node.getHash(), MerkleHash.fromHashCode(paddedHash("11a99"))));
    }

    @Test
    public void testTwoLeavesInTree() throws Exception {
        ExistenceProof proof = new ExistenceProof();
        LeafNode node = LeafNode.fromData(new byte[]{0xa});
        LeafNode rightSibling = LeafNode.fromData(new byte[]{0xb});

        proof.addRightSibling(rightSibling.getHash());

        assertTrue(proof.validate(node.getHash(), MerkleHash.fromHashCode(paddedHash("11a91b99"))));
    }

    @Test
    public void testLeafInTreeWithTwoLevels() throws Exception {
        ExistenceProof proof = new ExistenceProof();
        LeafNode node = LeafNode.fromData(new byte[]{0xa});
        LeafNode firstRightSibling = LeafNode.fromData(new byte[]{0xb});

        proof.addRightSibling(firstRightSibling.getHash());
        proof.addLeftSibling(MerkleHash.fromHashCode(paddedHash("11c91d99")));

        assertTrue(proof.validate(node.getHash(), MerkleHash.fromHashCode(paddedHash("111c91d9911a91b999"))));
    }

    @Test
    public void testLeafNotInTree() throws Exception {
        ExistenceProof proof = new ExistenceProof();
        LeafNode node = LeafNode.fromData(new byte[]{0xa});
        LeafNode rightSibling = LeafNode.fromData(new byte[]{0xb});

        proof.addRightSibling(rightSibling.getHash());

        assertFalse(proof.validate(node.getHash(), MerkleHash.fromHashCode(paddedHash("11c91b99"))));
    }

    @Test
    public void testLeafInTreeButSiblingIsNot() throws Exception {
        ExistenceProof proof = new ExistenceProof();
        LeafNode node = LeafNode.fromData(new byte[]{0xa});
        LeafNode rightSibling = LeafNode.fromData(new byte[]{0xb});

        proof.addRightSibling(rightSibling.getHash());

        assertFalse(proof.validate(node.getHash(), MerkleHash.fromHashCode(paddedHash("11a91c99"))));
    }

    @Test
    public void testLeafInTreeButSiblingAddedWithWrongSide() throws Exception {
        ExistenceProof proof = new ExistenceProof();
        LeafNode node = LeafNode.fromData(new byte[]{0xa});
        LeafNode rightSibling = LeafNode.fromData(new byte[]{0xb});

        proof.addLeftSibling(rightSibling.getHash());

        assertFalse(proof.validate(node.getHash(), MerkleHash.fromHashCode(paddedHash("11a91b99"))));
    }

    @Test
    public void testLeafWithDeletedLeftSibling() throws Exception {
        ExistenceProof proof = new ExistenceProof();
        LeafNode node = LeafNode.fromData(new byte[]{0xb});

        // Leaves are a, b and c, but then we remove a, so b's left sibling is now null.
        proof.addLeftSibling(null);
        proof.addRightSibling(MerkleHash.fromHashCode(paddedHash("11c99")));

        assertTrue(proof.validate(node.getHash(), MerkleHash.fromHashCode(paddedHash("111b9911c999"))));
    }

    @Test
    public void testLeafWithDeletedRightSibling() throws Exception {
        ExistenceProof proof = new ExistenceProof();
        LeafNode node = LeafNode.fromData(new byte[]{0xa});

        // Leaves are a, b and c, but then we remove b, so a's right sibling is now null.
        proof.addRightSibling(null);
        proof.addRightSibling(MerkleHash.fromHashCode(paddedHash("11c99")));

        assertTrue(proof.validate(node.getHash(), MerkleHash.fromHashCode(paddedHash("111a9911c999"))));
    }
}
