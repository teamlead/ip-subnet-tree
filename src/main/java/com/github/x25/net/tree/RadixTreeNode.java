package com.github.x25.net.tree;

public class RadixTreeNode<V> {

    private RadixTreeNode<V> right;
    private RadixTreeNode<V> left;
    private V value;

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public RadixTreeNode<V> getRight() {
        return right;
    }

    public void setRight(RadixTreeNode<V> right) {
        this.right = right;
    }

    public RadixTreeNode<V> getLeft() {
        return left;
    }

    public void setLeft(RadixTreeNode<V> left) {
        this.left = left;
    }
}
