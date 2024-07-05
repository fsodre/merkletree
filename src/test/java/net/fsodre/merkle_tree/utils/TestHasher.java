package net.fsodre.merkle_tree.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import net.fsodre.merkle_tree.hashers.Hasher;
import net.fsodre.merkle_tree.hashers.MerkleHash;

/**
 * Provides a Hasher implementation that provides semi-human-readable hashes to
 * be used in unit tests. The idea behind this hash algorithm is rather trivial:
 * we define it as H(x) = "H(x)" (that is, we simply add "H()" around the
 * input). However, since the final hash code needs to be a valid hexadecimal
 * string (so no 'H', '(' or ')'), we replace "H(" with "1" and ")" with "9",
 * therefore H(x) = "1x9". Notice that in a Merkle tree we'll observe recursive
 * hashes of hashes concatenations, like H(H(a)(b)), which will be encoded as
 * "11a91b99". Since the final hashes also need to have a certain size, the
 * algorith fills the remaining bytes with '0's.
 *
 * WARNING: This hasher should obviously not be used in production, and it won't
 * work for input data that have "1" or "9" digits in it.
 */
final public class TestHasher implements Hasher {

    /**
     * Creates a MerkleHash by hashing the data provided as an array of bytes.
     */
    @Override
    public MerkleHash hash(byte[] bytes) {
        return MerkleHash.fromHashCode(hashByteArray(bytes));
    }

    /**
     * Creates a MerkleHash by hashing the data read from a stream.
     */
    @Override
    public MerkleHash hash(InputStream stream) throws IOException {
        return hash(stream.readAllBytes());
    }

    /**
     * Fills the string with '0' at the end to make it have the expected size.
     */
    public static byte[] paddedHash(String hash) throws DecoderException {
        if (hash.length() < MerkleHash.EXPECTED_SIZE_IN_CHARS) {
            String format = "%-" + String.valueOf(MerkleHash.EXPECTED_SIZE_IN_CHARS) + "s";
            hash = String.format(format, hash).replace(' ', '0');
        }
        return Hex.decodeHex(hash);
    }

    private static byte[] hashByteArray(byte[] input) {
        String strHash = Hex.encodeHexString(input);
        String result = strHash.chars()
                .filter(c -> c != '0')
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());

        assert result.length() < MerkleHash.EXPECTED_SIZE_IN_CHARS - 2 : "Test hash is too long.";

        String hashed = String.format("1%s9", result);

        try {
            return paddedHash(hashed);
        } catch (DecoderException e) {
            throw new RuntimeException("This should never happen");
        }
    }
}
