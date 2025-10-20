
/****************************************************
 * @file: Node.java
 * @description: BST Node class that stores a value and has reference to left/right child.
 * @author: Tim Hultman
 * @date: 9/14/25
 ****************************************************/
public class Node<T extends Comparable<T>> implements Comparable<Node<T>> {
    public T val;
    public Node<T> left;
    public Node<T> right;

    /**
     * Constructor to create a new node with a given value.
     * Parameter T value, value in  node
     */
    public Node(T num) {
        this.val = num;
    }

    //Getter for current node
    public T getValue() {
        return val;
    }
    //Setter for node
    public void setValue(T num) {
        this.val = num;
    }

    //Left child getter
    public Node<T> getLeft() {
        return left;
    }

    //Right child getter
    public Node<T> getRight() {
        return right;
    }
    //Left cjild setter
    public void setLeft(Node<T> lt) {
        this.left = lt;
    }

    //right child setter
    public void setRight(Node<T> rt) {
        this.right = rt;
    }

    /**
     * Compares this node with another node based on values
     * Parameter Node<T> other, other node
     * Return int value that is negative, positive, or 0 depending on comparison
     */
    @Override
    public int compareTo(Node<T> otherNode) {
        return this.val.compareTo(otherNode.val);
    }
}