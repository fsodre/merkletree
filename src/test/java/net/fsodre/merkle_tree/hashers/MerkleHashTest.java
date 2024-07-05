package net.fsodre.merkle_tree.hashers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.fsodre.merkle_tree.utils.TestHasher;
import static net.fsodre.merkle_tree.utils.TestHasher.paddedHash;

public class MerkleHashTest {

    private final String VALID_HASH_CODE = "0123456789012345678901234567890123456789012345678901234567890123"; // 256 bits (UTF-8)
    private final String DIFFERENT_VALID_HASH_CODE = "9876543210987654321098765432109876543210987654321098765432109876"; // 256 bits (UTF-8)

    @BeforeAll
    public static void init() {
        HasherProvider.initialize(new TestHasher());
    }

    @Test
    public void testHashFromHashCode() throws Exception {
        MerkleHash hash = MerkleHash.fromHashCode(VALID_HASH_CODE);

        assertEquals(VALID_HASH_CODE, hash.toString());
        assertArrayEquals(Hex.decodeHex(VALID_HASH_CODE), hash.toBytes());
    }

    @Test
    public void testHashFromData() throws Exception {
        MerkleHash hash = MerkleHash.fromData(new byte[]{0xa});

        assertEquals(Hex.encodeHexString(paddedHash("1a9")), hash.toString());
    }

    @Test
    public void testHashFromStream() throws Exception {
        InputStream stream = new ByteArrayInputStream(new byte[]{0xa});

        MerkleHash hash = MerkleHash.fromStream(stream);

        assertEquals(Hex.encodeHexString(paddedHash("1a9")), hash.toString());
    }

    @Test
    public void testInvalidHashSize() throws Exception {
        @SuppressWarnings("unused")
        Throwable unused = assertThrows(DecoderException.class, () -> {
            MerkleHash.fromHashCode(VALID_HASH_CODE + "0");
        });
    }

    @Test
    public void testConcatenation() throws Exception {
        MerkleHash hash1 = MerkleHash.fromHashCode(VALID_HASH_CODE);
        MerkleHash hash2 = MerkleHash.fromHashCode(DIFFERENT_VALID_HASH_CODE);

        assertArrayEquals(Hex.decodeHex(VALID_HASH_CODE + DIFFERENT_VALID_HASH_CODE), hash1.concat(hash2));
        assertArrayEquals(Hex.decodeHex(DIFFERENT_VALID_HASH_CODE + VALID_HASH_CODE), hash2.concat(hash1));
    }

    @Test
    public void testConcatenationWithNull() throws Exception {
        MerkleHash hash = MerkleHash.fromHashCode(VALID_HASH_CODE);

        assertArrayEquals(Hex.decodeHex(VALID_HASH_CODE), hash.concat(null));
    }

    @Test
    public void testSerialization() throws Exception {
        MerkleHash hash = MerkleHash.fromHashCode(VALID_HASH_CODE);

        assertArrayEquals(Hex.decodeHex(VALID_HASH_CODE), hash.concat(null));
    }
}
