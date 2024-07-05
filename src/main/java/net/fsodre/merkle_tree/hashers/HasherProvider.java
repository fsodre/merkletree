package net.fsodre.merkle_tree.hashers;

/**
 * Used to define which hash algorithm the tree must work with.
 */
public class HasherProvider {

    private static Hasher hasher = new Sha256Hasher();

    public static void setHasher(Hasher hasher) {
        HasherProvider.hasher = hasher;
    }

    public static Hasher getHasher() {
        return hasher;
    }
}
