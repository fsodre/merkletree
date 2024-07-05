# Merkle Tree

A Merkle tree is a tree structure in which every node is identified by the hash of the concatenation of its children's ids. Each leaf gets its id by hashing a single data block associated with it.

Merkle trees are widely used in data synchronization applications (like P2P data transferring) and situations in which we need to easily verify that a block of data belongs to a much larger dataset without the need to have all the original data at hand.

## Dynamic Merkle tree

This implementation of Merkle Tree is a dynamic one, meaning a tree can be manipulated as to freely add, modify and delete leaf nodes.

### Deletions

Deleted leaves will leave an empty space in the list of leaves. We chose not to realocate other leaves when one is deleted (by shifting them to the left) so 
that we don't need to recompute hashes of the ancestors of the leaves that were moved. Instead, we keep track of the empty spaces and give them to new nodes that are eventually added afterwards.

## Hash Function

This implementation uses `SHA-256` as hashing strategy by default. However, it's easy to add and use others by implementing a `Hasher` interface and configuring it with `HasherProvider`.

```
final public class MyHasher implements Hasher {
    ...
} 

public class Main {
    public final static void main(String[] args) {
        HasherProvider.setHasher(new MyHasher());
        ...
    }
}

```

## Verifications

This implementation only provides proofs of existence (`MerkleTree.buildExistenceProof()`).

## Building, testing and running the example

```
$ mvn build
$ mvn test
$ mvn exec:java
```