package com.github.x25.net.tree.radix;

import com.github.x25.net.tree.radix.node.Node;

public class RadixInt32Tree<V> {

    private final Node<V> root = new Node<V>();

    public void insert(int key, int mask, V value) {
        Node<V> node, next;
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
            next = new Node<V>();

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

    public V find(int key) {
        long bit = 0x80000000L;
        V value;
        Node<V> node;

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
