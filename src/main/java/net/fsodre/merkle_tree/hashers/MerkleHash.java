package net.fsodre.merkle_tree.hashers;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * Represents a hash code used in the merkle tree nodes.
 */
final public class MerkleHash implements Serializable {

    private static final long serialVersionUID = 1L;

    // Bytes of the hash code.
    final private byte[] bytes;

    /**
     * Creates a MerkleHash by hashing data given as an array of bytes.
     */
    public static MerkleHash fromData(byte[] data) {
        return HasherProvider.getHasher().hash(data);
    }

    /**
     * Creates a MerkleHash by hashing data given as a String.
     */
    public static MerkleHash fromData(String data) {
        return fromData(data.getBytes());
    }

    /**
     * Creates a MerkleHash by hashing data read from a Stream
     *
     * @throws IOException upon issues reading from the stream.
     */
    public static MerkleHash fromStream(InputStream stream) throws IOException {
        return HasherProvider.getHasher().hash(stream);
    }

    /**
     * Creates a MerkleHash from the hash code itself, given as an array of
     * bytes.
     *
     * @throws AssertionError if the array of bytes doesn't have the expected
     * size.
     */
    public static MerkleHash fromHashCode(byte[] bytes) {
        assert bytes.length == expectedSizeInBytes() : "Invalid hash size upon creation";
        return new MerkleHash(bytes);
    }

    /**
     * Creates a MerkleHash from the hash code itself, given as a hex string.
     *
     * @throws DecoderException if the string isn't a valid hex string.
     * @throws AssertionError if the resulting array of bytes doesn't have the
     * expected size.
     */
    public static MerkleHash fromHashCode(String str) throws DecoderException {
        return fromHashCode(Hex.decodeHex(str));
    }

    /**
     * Returns the bytes representing the hash code.
     */
    public byte[] toBytes() {
        return this.bytes.clone();
    }

    /**
     * Returns the hexadecimal string representation of the hash code.
     */
    @Override
    public String toString() {
        return Hex.encodeHexString(bytes);
    }

    /**
     * Returns the concatenation of this MerkleHash with another one as an array
     * of bytes.
     */
    public byte[] concat(MerkleHash other) {
        if (other == null) {
            return bytes.clone();
        }
        byte[] result = new byte[2 * expectedSizeInBytes()];
        System.arraycopy(this.bytes, 0, result, 0, expectedSizeInBytes());
        System.arraycopy(other.bytes, 0, result, expectedSizeInBytes(), expectedSizeInBytes());
        return result;
    }

    public static int expectedSizeInBytes() {
        return HasherProvider.getHasher().outputBitsCount() / 8;
    }

    public static int expectedSizeInHexChars() {
        return HasherProvider.getHasher().outputBitsCount() / 4;
    }

    private MerkleHash(byte[] bytes) {
        this.bytes = bytes.clone();
    }
}
