package net.fsodre.merkle_tree.example;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.fsodre.merkle_tree.ExistenceProof;
import net.fsodre.merkle_tree.MerkleTree;
import net.fsodre.merkle_tree.hashers.MerkleHash;
import net.fsodre.merkle_tree.nodes.LeafNode;


public class Main {
    public static void main(String[] args) {
        MerkleTree tree = new MerkleTree();

        String[] data = new String[]{"Hello", "World", "how", "are", "you"};
        String[] streamedData = new String[]{"I", "am", "doing", "alright!"};

        List<ByteArrayInputStream> streams = Arrays.stream(streamedData).map(d -> new ByteArrayInputStream(d.getBytes())).collect(Collectors.toList());

        Arrays.stream(data).forEach(str -> tree.addLeaf(LeafNode.fromData(str)));
        streams.stream().forEach(stream -> {
            try {
                tree.addLeaf(LeafNode.fromStream(stream));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("Initial Root node: " + tree.getRoot().getHash().toString());

        MerkleHash doingHash = MerkleHash.fromData("doing");
        ExistenceProof proofForDoing = tree.buildExistenceProof(doingHash);
        System.out.println("Is 'doing' in the tree? " + String.valueOf(proofForDoing.validate(doingHash, tree.getRoot().getHash())));
        tree.removeLeaf(doingHash);
        System.out.println("Is 'doing' still in the tree? " + String.valueOf(proofForDoing.validate(doingHash, tree.getRoot().getHash())));

        MerkleHash newWordHash = MerkleHash.fromData("new word");
        ExistenceProof proofForNewWord = tree.buildExistenceProof(newWordHash);
        assert proofForNewWord == null: "shouldn't be able to create proof for `new word`";
        tree.updateLeafAt(2, LeafNode.fromData("new word"));
        proofForNewWord = tree.buildExistenceProof(newWordHash);
        System.out.println("Is 'new word' now in the tree? " + String.valueOf(proofForNewWord.validate(newWordHash, tree.getRoot().getHash())));

        System.out.println("Final Root node: " + tree.getRoot().getHash().toString());
    }
}
