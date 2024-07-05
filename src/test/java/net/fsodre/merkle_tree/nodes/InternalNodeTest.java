package net.fsodre.merkle_tree.nodes;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.fsodre.merkle_tree.hashers.HasherProvider;
import net.fsodre.merkle_tree.utils.TestHasher;
import static net.fsodre.merkle_tree.utils.TestHasher.paddedHash;

public class InternalNodeTest {

    @BeforeAll
    public static void setUpSuite() {
        HasherProvider.initialize(new TestHasher());
    }

    @Test
    public void testChildrenAreLeafs() throws Exception {
        LeafNode left = LeafNode.fromData(new byte[]{0xa});
        LeafNode right = LeafNode.fromData(new byte[]{0xb});

        InternalNode internal = InternalNode.from(left, right);

        assertArrayEquals(paddedHash("11a91b99"), internal.getHash().toBytes());
    }

    @Test
    public void testOneChild() throws Exception {
        LeafNode child = LeafNode.fromData(new byte[]{0xa});

        InternalNode internal = InternalNode.from(child);

        assertArrayEquals(paddedHash("11a99"), internal.getHash().toBytes());
    }

    @Test
    public void testNoChild() throws Exception {
        InternalNode internal = InternalNode.from(null);

        assertNull(internal.getHash());
    }

    @Test
    public void testChildrenAreInternals() throws Exception {
        LeafNode leftA = LeafNode.fromData(new byte[]{0xa});
        LeafNode rightA = LeafNode.fromData(new byte[]{0xb});
        LeafNode leftB = LeafNode.fromData(new byte[]{0xc});
        LeafNode rightB = LeafNode.fromData(new byte[]{0xd});
        InternalNode internalA = InternalNode.from(leftA, rightA);
        InternalNode internalB = InternalNode.from(leftB, rightB);

        InternalNode internalParent = InternalNode.from(internalA, internalB);

        assertArrayEquals(internalParent.getHash().toBytes(), paddedHash("111a91b9911c91d999"));
    }
}
