package net.fsodre.merkle_tree.hashers;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.digest.DigestUtils;

public class Sha512256Hasher implements Hasher {

    /**
     * Returns a MerkleHash created by hashing data in local memory using
     * SHA512-256.
     */
    @Override
    public MerkleHash hash(byte[] data) {
        return MerkleHash.fromHashCode(DigestUtils.sha512_256(data));
    }

    /**
     * Returns a MerkleHash created by hashing data read from a stream using
     * SHA512-256.
     *
     * @throws IOException upon issues reading the data from the stream.
     */
    @Override
    public MerkleHash hash(InputStream inputStream) throws IOException {
        return MerkleHash.fromHashCode(DigestUtils.sha512_256(inputStream));
    }

    /**
     * Returns the number of bits output by SHA512-256.
     *
     * @throws IOException upon issues reading the data from the stream.
     */
    @Override
    public int outputBitsCount() {
        return 256;
    }
    
}
