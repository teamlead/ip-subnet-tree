package com.github.x25.net.tree.radix.node;

public class Node<V> {

    private volatile Node<V> right;
    private volatile Node<V> left;
    private volatile V value;

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public Node<V> getRight() {
        return right;
    }

    public void setRight(Node<V> right) {
        this.right = right;
    }

    public Node<V> getLeft() {
        return left;
    }

    public void setLeft(Node<V> left) {
        this.left = left;
    }
}
