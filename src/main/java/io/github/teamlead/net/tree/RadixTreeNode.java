package io.github.teamlead.net.tree;

/**
 * Represents a single node within a RadixInt32Tree, designed to hold values associated with 32-bit integer keys.
 * Each node potentially has two children, referred to as the left and right child nodes, representing the binary
 * choices at each step in the tree based on the bits of the key. This class also encapsulates the value associated
 * with a node, which can represent the endpoint of a key or intermediate data in the radix tree structure.
 *
 * @param <V> the type of the value that the node holds
 */
public class RadixTreeNode<V> {

    private RadixTreeNode<V> right;
    private RadixTreeNode<V> left;
    private V value;

    /**
     * Retrieves the value associated with this node.
     *
     * @return the value held by this node, which may be {@code null} if the node does not hold a value
     */
    public V getValue() {
        return value;
    }

    /**
     * Sets or updates the value associated with this node.
     *
     * @param value the new value to be associated with this node
     */
    public void setValue(V value) {
        this.value = value;
    }

    /**
     * Retrieves the right child of this node.
     *
     * @return the right child node, which may be {@code null} if no right child exists
     */
    public RadixTreeNode<V> getRight() {
        return right;
    }

    /**
     * Sets or updates the right child of this node.
     *
     * @param right the node to be set as the right child of this node
     */
    public void setRight(RadixTreeNode<V> right) {
        this.right = right;
    }

    /**
     * Retrieves the left child of this node.
     *
     * @return the left child node, which may be {@code null} if no left child exists
     */
    public RadixTreeNode<V> getLeft() {
        return left;
    }

    /**
     * Sets or updates the left child of this node.
     *
     * @param left the node to be set as the left child of this node
     */
    public void setLeft(RadixTreeNode<V> left) {
        this.left = left;
    }
}
