package net.fsodre.merkle_tree.hashers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.codec.digest.DigestUtils;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import org.junit.jupiter.api.Test;

public class Sha256HasherTest {

    private final Sha256Hasher hasher = new Sha256Hasher();

    @Test
    public void testSha256Hashing() {
        MerkleHash hash = hasher.hash("a".getBytes());

        assertArrayEquals(hash.toBytes(), DigestUtils.sha256("a"));
    }

    @Test
    public void testSha256StreamHashing() throws Exception {
        InputStream stream = new ByteArrayInputStream("a".getBytes());

        MerkleHash hash = hasher.hash(stream);

        assertArrayEquals(hash.toBytes(), DigestUtils.sha256("a"));
    }
}
