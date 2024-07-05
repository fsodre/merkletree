package net.fsodre.merkle_tree.nodes;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.fsodre.merkle_tree.hashers.HasherProvider;
import net.fsodre.merkle_tree.utils.TestHasher;
import static net.fsodre.merkle_tree.utils.TestHasher.paddedHash;

public class LeafNodeTest {

    @BeforeAll
    public static void setUpSuite() {
        HasherProvider.initialize(new TestHasher());
    }

    @Test
    public void testLeafCreationFromData() throws Exception {
        LeafNode leaf = LeafNode.fromData(new byte[]{0xa});
        assertArrayEquals(paddedHash("1a9"), leaf.getHash().toBytes());
    }

    @Test
    public void testLeafCreationFromStream() throws Exception {
        InputStream stream = new ByteArrayInputStream(new byte[]{0xa});

        LeafNode leaf = LeafNode.fromStream(stream);

        assertArrayEquals(paddedHash("1a9"), leaf.getHash().toBytes());
    }
}
