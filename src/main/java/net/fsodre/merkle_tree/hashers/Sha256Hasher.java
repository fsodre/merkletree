package net.fsodre.merkle_tree.hashers;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * An implementation of Hasher interface that uses SHA-256 to hash data.
 */
public class Sha256Hasher implements Hasher {

    /**
     * Returns a MerkleHash created by hashing data in local memory using
     * SHA256.
     */
    @Override
    public MerkleHash hash(byte[] data) {
        return MerkleHash.fromHashCode(DigestUtils.sha256(data));
    }

    /**
     * Returns a MerkleHash created by hashing data read from a stream using
     * SHA256.
     *
     * @throws IOException upon issues reading the data from the stream.
     */
    @Override
    public MerkleHash hash(InputStream inputStream) throws IOException {
        return MerkleHash.fromHashCode(DigestUtils.sha256(inputStream));
    }

}
