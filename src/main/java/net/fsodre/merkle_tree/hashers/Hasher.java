package net.fsodre.merkle_tree.hashers;

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a hashing algorithm. Needs to be able to hash data in local memory
 * as well as data coming from possibily buffered input streams.
 */
public interface Hasher {

    /**
     * Hashes the data provided as a byte array.
     */
    MerkleHash hash(byte[] bytes);

    /**
     * Hashes the data provided through a stream.
     */
    MerkleHash hash(InputStream inputStream) throws IOException;
}
