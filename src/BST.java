

/****************************************************
 * @file: BST.java
 * @description: Binary Search Tree class that has insertion, searching, removal, and traversal. Uses Noda.java
 * @author: Tim Hultman
 * @date: 9/23/25
 ****************************************************/

import java.util.Iterator;
import java.util.Stack;

public class BST<T extends Comparable<T>> implements Iterable<T> {
    private Node<T> rt;

    /** Constructor for BST */
    public BST() {
        rt = null;
    }

    /**
     * Inserts value into BST
     * T value for BST insertion
     */
    public void insert(T val) {
        rt = insertR(rt, val);
    }
    /**
     * Recursive helper method for inserting a value into the BST
     * Parameter Node<T> node, subtree root
     * Parameter T value,  the value to insert
     * Return Node<T>, new subtree root after insertion
     */
    private Node<T> insertR(Node<T> nd, T num) {
        if (nd ==null) {
            return new Node<>(num);
        }
        if (num.compareTo(nd.getValue()) > 0) {
            nd.setRight(insertR(nd.getRight(), num));
        }
        if (num.compareTo(nd.getValue()) < 0) {
            nd.setLeft(insertR(nd.getLeft(), num));
        }

        return nd;
    }

    /**
     * Searches for a value in the BST
     * Parameter T value, value to search for
     * Returns Node<T>, node containing the value/null if not found
     */
    public Node<T> search(T val) {
        return searchR(rt, val);
    }

    /**
     * Recursive helper method for searching a BST
     * Parameter Node<T> node, subtree root
     * Parameter T value,  the value to search for
     * Return Node<T>, node with value
     */
    private Node<T> searchR(Node<T> node, T val) {
        if (node == null) {
            return null;
        }
        if (val.compareTo(node.getValue()) == 0) {
            return node;
        }
        if (val.compareTo(node.getValue()) < 0) {
            return searchR(node.getLeft(), val);
        }
        else {
            return searchR(node.getRight(), val);
        }
    }

    /**
     * Removes a value from the BST
     * Parameter T value, value to remove
     * Return Node<T>, removed node/null if not found
     */
    public Node<T> remove(T val) {
        Node<T>[] result = new Node[1];
        rt = removeR(rt, val, result);
        return result[0];
    }

    /**
     * Recursive helper method for removing a value
     * Parameter Node<T> node, subtree root
     * Parameter T value, value to remove
     * Parameter Node<T>[] result, stores removed node
     * Return Node<T>, updated subtree root
     */
    public Node<T> removeR(Node<T> node, T val, Node<T>[] result) {
        if (node ==null) {
            return null;
        }
        if (val.compareTo(node.getValue()) <= -1) {
            node.setLeft(removeR(node.getLeft(), val, result));
        }
        else if (val.compareTo(node.getValue()) >= 1) {
            node.setRight(removeR(node.getRight(), val, result));
        }
        else {
            result[0] = node;
            if (node.getLeft() == null) {
                return node.getRight();
            }
            if (node.getRight() ==null){
                return node.getLeft();
            }
            Node<T> min = findMin(node.getRight());
            node.setValue(min.getValue());  // replace value, don’t create new node
            node.setRight(deleteMin(node.getRight()));
        }
        return node;
    }
    /**
     * Delete minimum value node in a subtree
     * Parameter Node<T> node, subtree root
     * Return Node<T>, updated subtree root
     */
    public Node<T> deleteMin(Node<T> node) {
        if (node ==null) {
            return null;
        }
        if (node.getLeft() ==null){
            return node.getRight();
        }
        node.setLeft(deleteMin(node.getLeft()));
        return node;
    }

    /**
     * Find minimum value node in a subtree
     * Parameter Node<T> node, subtree root
     * Return Node<T>, node with smallest value
     */
    public Node<T> findMin(Node<T> node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node;
    }


    /**
     * Returns an iterator for in order traversal of the BST
     * Return Iterator<T>, iterator over values in ascending order
     */
    @Override
    public Iterator<T> iterator() {
        return new InOrderIterator(rt);
    }

    /**
     * Inner class for in-order traversal using a stack
     */
    public class InOrderIterator implements Iterator<T> {
        public Stack<Node<T>> stack = new Stack<>();

        /** Constructor: initialize iterator at root */
        public InOrderIterator(Node<T> rt) {
            pushLeft(rt);
        }

        /**
         * Push all left children of a subtree onto stack
         * Parameter Node<T> node, starting node
         */
        public void pushLeft(Node<T> node) {
            while (node != null) {
                stack.push(node);
                node = node.getLeft();
            }
        }

        /** Return boolean, true if more nodes exist */
        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        /** Return T, next smallest value in BST */
        @Override
        public T next() {
            Node<T> node = stack.pop();
            pushLeft(node.getRight());
            return node.getValue();
        }
    }
}
