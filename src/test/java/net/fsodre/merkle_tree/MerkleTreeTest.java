package net.fsodre.merkle_tree;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.fsodre.merkle_tree.hashers.HasherProvider;
import net.fsodre.merkle_tree.nodes.LeafNode;
import net.fsodre.merkle_tree.utils.TestHasher;
import static net.fsodre.merkle_tree.utils.TestHasher.paddedHash;

public class MerkleTreeTest {

    private MerkleTree tree;

    @BeforeAll
    public static void setUpSuite() {
        HasherProvider.initialize(new TestHasher());
    }

    @BeforeEach
    public void setUp() {
        tree = new MerkleTree();
    }

    @Test
    public void testAddSingleLeaf() throws Exception {
        int pos = tree.addLeaf(LeafNode.fromData(new byte[]{0xa}));

        assertArrayEquals(paddedHash("11a99"), tree.getRoot().getHash().toBytes());
        assertEquals(0, pos);
    }

    @Test
    public void testAddTwoLeafs() throws Exception {
        int posA = tree.addLeaf(LeafNode.fromData(new byte[]{0xa}));
        int posB = tree.addLeaf(LeafNode.fromData(new byte[]{0xb}));

        assertArrayEquals(paddedHash("11a91b99"), tree.getRoot().getHash().toBytes());
        assertEquals(0, posA);
        assertEquals(1, posB);
    }

    @Test
    public void testAddThreeLeafsTwoLevels() throws Exception {
        int posA = tree.addLeaf(LeafNode.fromData(new byte[]{0xa}));
        int posB = tree.addLeaf(LeafNode.fromData(new byte[]{0xb}));
        int posC = tree.addLeaf(LeafNode.fromData(new byte[]{0xc}));

        assertArrayEquals(paddedHash("111a91b9911c999"), tree.getRoot().getHash().toBytes());
        assertEquals(0, posA);
        assertEquals(1, posB);
        assertEquals(2, posC);
    }

    @Test
    public void testAddNullLeaves() throws Exception {
        int pos = tree.addLeaf(null);

        assertNull(tree.getRoot().getHash());
        assertEquals(0, pos);
    }

    @Test
    public void testUpdateLeafWithHash() throws Exception {
        LeafNode leafA = LeafNode.fromData(new byte[]{0xa});
        tree.addLeaf(leafA);
        tree.addLeaf(LeafNode.fromData(new byte[]{0xb}));
        tree.addLeaf(LeafNode.fromData(new byte[]{0xc}));

        tree.updateLeaf(leafA.getHash(), LeafNode.fromData(new byte[]{0xd}));

        assertArrayEquals(paddedHash("111d91b9911c999"), tree.getRoot().getHash().toBytes());
    }

    @Test
    public void testUpdateLeafWithPosition() throws Exception {
        tree.addLeaf(LeafNode.fromData(new byte[]{0xa}));
        tree.addLeaf(LeafNode.fromData(new byte[]{0xb}));
        tree.addLeaf(LeafNode.fromData(new byte[]{0xc}));

        tree.updateLeafAt(1, LeafNode.fromData(new byte[]{0xd}));

        assertArrayEquals(paddedHash("111a91d9911c999"), tree.getRoot().getHash().toBytes());
    }

    @Test
    public void testUpdateTwoLeafs() throws Exception {
        LeafNode leafA = LeafNode.fromData(new byte[]{0xa});
        tree.addLeaf(leafA);
        tree.addLeaf(LeafNode.fromData(new byte[]{0xb}));
        int posC = tree.addLeaf(LeafNode.fromData(new byte[]{0xc}));

        tree.updateLeaf(leafA.getHash(), LeafNode.fromData(new byte[]{0xd}));
        tree.updateLeafAt(posC, LeafNode.fromData(new byte[]{0xe}));

        assertArrayEquals(paddedHash("111d91b9911e999"), tree.getRoot().getHash().toBytes());
    }

    @Test
    public void testUpdateNullLeaf() throws Exception {
        tree.addLeaf(null);

        tree.updateLeafAt(0, LeafNode.fromData(new byte[]{0xa}));

        assertArrayEquals(paddedHash("11a99"), tree.getRoot().getHash().toBytes());
    }

    @Test
    public void testEmptyTree() throws Exception {
        assertNull(tree.getRoot());
    }

    @Test
    public void testRemoveLeafWithHash() throws Exception {
        LeafNode leafA = LeafNode.fromData(new byte[]{0xa});
        tree.addLeaf(leafA);
        tree.addLeaf(LeafNode.fromData(new byte[]{0xb}));
        tree.addLeaf(LeafNode.fromData(new byte[]{0xc}));

        tree.removeLeaf(leafA.getHash());

        assertArrayEquals(paddedHash("111b9911c999"), tree.getRoot().getHash().toBytes());
    }

    @Test
    public void testRemoveLeafWithPosition() throws Exception {
        LeafNode leafA = LeafNode.fromData(new byte[]{0xa});
        tree.addLeaf(leafA);
        int posB = tree.addLeaf(LeafNode.fromData(new byte[]{0xb}));
        tree.addLeaf(LeafNode.fromData(new byte[]{0xc}));

        tree.removeLeafAt(posB);

        assertArrayEquals(paddedHash("111a9911c999"), tree.getRoot().getHash().toBytes());
    }

    @Test
    public void testRemoveTwoLeafs() throws Exception {
        LeafNode leafA = LeafNode.fromData(new byte[]{0xa});
        tree.addLeaf(leafA);
        tree.addLeaf(LeafNode.fromData(new byte[]{0xb}));
        int posC = tree.addLeaf(LeafNode.fromData(new byte[]{0xc}));

        tree.removeLeaf(leafA.getHash());
        tree.removeLeafAt(posC);

        assertArrayEquals(paddedHash("111b999"), tree.getRoot().getHash().toBytes());
    }

    @Test
    public void testReuseDeletedLeaf() throws Exception {
        int posA = tree.addLeaf(LeafNode.fromData(new byte[]{0xa}));
        tree.addLeaf(LeafNode.fromData(new byte[]{0xb}));
        tree.addLeaf(LeafNode.fromData(new byte[]{0xc}));

        tree.removeLeafAt(posA);
        tree.addLeaf(LeafNode.fromData(new byte[]{0xd}));

        assertArrayEquals(paddedHash("111d91b9911c999"), tree.getRoot().getHash().toBytes());
    }

    @Test
    public void testReuseTwoDeletedLeaf() throws Exception {
        LeafNode leafA = LeafNode.fromData(new byte[]{0xa});
        tree.addLeaf(leafA);
        tree.addLeaf(LeafNode.fromData(new byte[]{0xb}));
        int posC = tree.addLeaf(LeafNode.fromData(new byte[]{0xc}));

        tree.removeLeaf(leafA.getHash());
        tree.removeLeafAt(posC);
        // Will get C's spot
        tree.addLeaf(LeafNode.fromData(new byte[]{0xd}));
        // Will get A's spot
        tree.addLeaf(LeafNode.fromData(new byte[]{0xe}));
        // Will be added to a new spot
        tree.addLeaf(LeafNode.fromData(new byte[]{0xf}));

        assertArrayEquals(paddedHash("111e91b9911d91f999"), tree.getRoot().getHash().toBytes());
    }

    @Test
    public void testExistenceProofForExistingLeafs() throws Exception {
        LeafNode leafA = LeafNode.fromData(new byte[]{0xa});
        tree.addLeaf(leafA);
        LeafNode leafB = LeafNode.fromData(new byte[]{0xb});
        tree.addLeaf(leafB);
        LeafNode leafC = LeafNode.fromData(new byte[]{0xc});
        tree.addLeaf(leafC);
        LeafNode leafD = LeafNode.fromData(new byte[]{0xd});
        tree.addLeaf(leafD);
        LeafNode leafE = LeafNode.fromData(new byte[]{0xe});
        tree.addLeaf(leafE);

        ExistenceProof proofA = tree.buildExistenceProof(leafA.getHash());
        ExistenceProof proofB = tree.buildExistenceProof(leafB.getHash());
        ExistenceProof proofC = tree.buildExistenceProof(leafC.getHash());
        ExistenceProof proofD = tree.buildExistenceProof(leafD.getHash());
        ExistenceProof proofE = tree.buildExistenceProof(leafE.getHash());

        assertTrue(proofA.validate(leafA.getHash(), tree.getRoot().getHash()));
        assertTrue(proofB.validate(leafB.getHash(), tree.getRoot().getHash()));
        assertTrue(proofC.validate(leafC.getHash(), tree.getRoot().getHash()));
        assertTrue(proofD.validate(leafD.getHash(), tree.getRoot().getHash()));
        assertTrue(proofE.validate(leafE.getHash(), tree.getRoot().getHash()));
    }

    @Test
    public void testExistenceProofForNonExistingLeafs() throws Exception {
        tree.addLeaf(LeafNode.fromData(new byte[]{0xa}));
        LeafNode notInTree = LeafNode.fromData(new byte[]{0xb});

        ExistenceProof proof = tree.buildExistenceProof(notInTree.getHash());

        assertNull(proof);
    }

    @Test
    public void testExistenceProofInEmptyTree() throws Exception {
        LeafNode notInTree = LeafNode.fromData(new byte[]{0xa});

        ExistenceProof proof = tree.buildExistenceProof(notInTree.getHash());

        assertNull(proof);
    }

    @Test
    public void testExistenceProofInTreeWithNullLeaves() throws Exception {
        tree.addLeaf(null);
        LeafNode notInTree = LeafNode.fromData(new byte[]{0xa});

        ExistenceProof proof = tree.buildExistenceProof(notInTree.getHash());

        assertNull(proof);
    }
}
