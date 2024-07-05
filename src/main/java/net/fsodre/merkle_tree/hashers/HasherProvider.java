package net.fsodre.merkle_tree.hashers;

/**
 * Used to define which hash algorithm the tree must work with.
 */
public class HasherProvider {

    private static Hasher hasher;

    public static void initialize(Hasher hasher) {
        HasherProvider.hasher = hasher;
    }

    public static Hasher getHasher() {
        assert hasher != null : "Using a non initialized hasher";
        return hasher;
    }
}
