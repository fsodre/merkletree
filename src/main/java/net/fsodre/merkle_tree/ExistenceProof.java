package net.fsodre.merkle_tree;

import java.io.Serializable;
import java.util.ArrayList;

import net.fsodre.merkle_tree.hashers.MerkleHash;

/**
 * Represents a proof of existence for a leaf node hash in a Merkle Tree. It's
 * basically a sequence of hashes and instructions on how they should be applied
 * in order to reach the root's hash code.
 */
final public class ExistenceProof implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * A Sibling is the sibling of a node that is part of the path between the
     * target leaf node and the root. It's represented by the hash of the
     * sibling node and an indicator telling if it's a sibling to the left or to
     * the right.
     */
    final private static class Sibling implements Serializable {
        private static final long serialVersionUID = 1L;

        public enum Side {
            LEFT,
            RIGHT,
        }

        private final MerkleHash hash;
        private final Side side;

        /**
         * Creates a sibling.
         */
        public Sibling(MerkleHash hash, Side side) {
            this.hash = hash;
            this.side = side;
        }

        /**
         * Returns the hash of the concatenation of this sibling with another
         * hash. The order in which the hashes are concatenated are defined by
         * the `side` field.
         */
        public MerkleHash combinedHash(MerkleHash otherHash) {
            return MerkleHash.fromData(concatenate(otherHash));
        }

        private byte[] concatenate(MerkleHash otherSibling) {
            if (hash == null && otherSibling == null) {
                return new byte[0];
            }
            if (side == Side.LEFT) {
                if (hash == null) {
                    return otherSibling.toBytes();
                }
                return hash.concat(otherSibling);
            } else {
                if (otherSibling == null) {
                    return hash.toBytes();
                }
                return otherSibling.concat(hash);
            }
        }
    }

    // The sequence of siblings that comprise the proof of existence.
    private final ArrayList<Sibling> proofSequence;

    /**
     * Creates a new proof of existence.
     */
    public ExistenceProof() {
        proofSequence = new ArrayList<>();
    }

    /**
     * Adds a left sibling to the proof.
     */
    public void addLeftSibling(MerkleHash hash) {
        proofSequence.add(new Sibling(hash, Sibling.Side.LEFT));
    }

    /**
     * Adds a right sibling to the proof.
     */
    public void addRightSibling(MerkleHash hash) {
        proofSequence.add(new Sibling(hash, Sibling.Side.RIGHT));
    }

    /**
     * Verifies that this proof indeed proves that the leaf with hash `target`
     * is indeed in the tree with root with hash `root`.
     */
    public boolean validate(MerkleHash target, MerkleHash root) {
        MerkleHash currentHash = target;
        for (Sibling sibling : proofSequence) {
            currentHash = sibling.combinedHash(currentHash);
        }
        return currentHash.toString().equals(root.toString());
    }
}
