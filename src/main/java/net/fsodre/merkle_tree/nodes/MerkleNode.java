package net.fsodre.merkle_tree.nodes;

import net.fsodre.merkle_tree.hashers.MerkleHash;

/**
 * Represents any node in the Merkle tree.
 */
public interface MerkleNode {

    MerkleHash getHash();
}
