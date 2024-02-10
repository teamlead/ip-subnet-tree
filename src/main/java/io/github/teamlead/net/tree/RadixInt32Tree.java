package io.github.teamlead.net.tree;

/**
 * A radix tree implementation specifically designed for storing and retrieving values based on 32-bit integer keys.
 * This implementation is optimized for IP routing table lookups, where the keys are often IP addresses represented
 * as integers and the masks denote the subnet sizes. The tree allows for efficient storage and retrieval by
 * compacting the common prefix of the keys, reducing the overall memory footprint and lookup time.
 *
 * @param <V> the type of the values that the tree nodes will hold
 */
public class RadixInt32Tree<V> {

    private RadixTreeNode<V> root;

    /**
     * Constructs a new, empty RadixInt32Tree. Initializes the root of the tree to a new node,
     * effectively setting up the tree for subsequent insertions and lookups.
     */
    public RadixInt32Tree() {
        root = new RadixTreeNode<V>();
    }

    /**
     * Inserts a value into the tree with a specified key and mask. The key is a 32-bit integer
     * representing the data to be stored, and the mask helps determine how the key is stored
     * within the tree's structure, affecting the node placement based on the key's significant bits.
     *
     * @param key the 32-bit integer key associated with the value to be inserted
     * @param mask the mask indicating the significant bits of the key for insertion
     * @param value the value to associate with the given key in the tree
     */
    public void insert(int key, int mask, V value) {
        RadixTreeNode<V> node, next;
        long bit = 0x80000000L;

        node = root;
        next = root;

        while ((bit & mask) != 0) {

            next = ((key & bit) != 0) ? node.getRight() : node.getLeft();

            if (next == null) {
                break;
            }

            bit >>= 1;
            node = next;
        }

        if (next != null) {
            node.setValue(value);
            return;
        }

        while ((bit & mask) != 0) {
            next = new RadixTreeNode<V>();

            next.setRight(null);
            next.setLeft(null);
            next.setValue(null);

            if ((key & bit) != 0) {
                node.setRight(next);

            } else {
                node.setLeft(next);
            }

            bit >>= 1;
            node = next;
        }

        node.setValue(value);
    }

    /**
     * Finds and returns the value associated with a given key. The search is performed based on
     * the exact match of the provided key against those stored in the tree. If no matching key is
     * found, this method returns {@code null}.
     *
     * @param key the 32-bit integer key for which to search in the tree
     * @return the value associated with the specified key, or {@code null} if no such key exists in the tree
     */
    public V find(int key) {
        long bit = 0x80000000L;
        V value;
        RadixTreeNode<V> node;

        value = null;
        node = root;

        while (node != null) {
            if (node.getValue() != null) {
                value = node.getValue();
            }

            if ((key & bit) != 0) {
                node = node.getRight();

            } else {
                node = node.getLeft();
            }

            bit >>= 1;
        }

        return value;
    }
}
