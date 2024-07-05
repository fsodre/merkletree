package net.fsodre.merkle_tree.nodes;

import java.io.IOException;
import java.io.InputStream;

import net.fsodre.merkle_tree.hashers.MerkleHash;

/**
 * Represents a leaf node in the Merkle tree. These nodes need to be created by
 * providing some data either in the form of byte array or a (possibily
 * buffered) stream.
 *
 * This implementation doesn't keep the source data reference in the node
 * itself.
 */
public class LeafNode implements MerkleNode {

    // MerkleHash associated with this node
    private final MerkleHash hash;

    /**
     * Creates a leaf node by providing the data as an array of bytes to be
     * hashed.
     */
    public static LeafNode fromData(byte[] bytes) {
        return new LeafNode(bytes);
    }

    /**
     * Creates a leaf node by providing the data as a String to be hashed.
     */
    public static LeafNode fromData(String data) {
        return fromData(data.getBytes());
    }

    /**
     * Creates a leaf node by providing the data read from a stream.
     */
    public static LeafNode fromStream(InputStream inputStream) throws IOException {
        return new LeafNode(inputStream);
    }

    /**
     * Returns the hash currently associated with this node.
     */
    @Override
    public MerkleHash getHash() {
        return hash;
    }

    private LeafNode(byte[] data) {
        this.hash = MerkleHash.fromData(data);
    }

    private LeafNode(InputStream inputStream) throws IOException {
        this.hash = MerkleHash.fromStream(inputStream);
    }
}
