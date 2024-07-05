package net.fsodre.merkle_tree.hashers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.codec.digest.DigestUtils;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.jupiter.api.Test;

public class Sha512256HasherTest {

    private final Sha512256Hasher hasher = new Sha512256Hasher();

    @Test
    public void testSha512256Hashing() {
        MerkleHash hash = hasher.hash("a".getBytes());

        assertArrayEquals(hash.toBytes(), DigestUtils.sha512_256("a"));
    }

    @Test
    public void testSha512256StreamHashing() throws Exception {
        InputStream stream = new ByteArrayInputStream("a".getBytes());

        MerkleHash hash = hasher.hash(stream);

        assertArrayEquals(hash.toBytes(), DigestUtils.sha512_256("a"));
    }
}
