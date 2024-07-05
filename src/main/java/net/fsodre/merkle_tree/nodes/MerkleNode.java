package net.fsodre.merkle_tree.nodes;

import java.io.Serializable;

import net.fsodre.merkle_tree.hashers.MerkleHash;

/**
 * Represents any node in the Merkle tree.
 */
public interface MerkleNode extends Serializable {

    MerkleHash getHash();
}
