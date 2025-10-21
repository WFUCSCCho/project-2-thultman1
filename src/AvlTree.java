/****************************************************
 * @file: AvlTree.java
 * @description: Generic AVL Tree implementation supporting insertion, removal, searching, and balancing.
 *               Ensures O(log N) operations through automatic rebalancing on inserts and deletions.
 * @acknowledgment:
 *   Portions of this code and documentation were developed
 *   with assistance from ChatGPT by OpenAI.
 * @author: Tim Hultman
 * @date: 10/19/25
 ****************************************************/

public class AvlTree<AnyType extends Comparable<? super AnyType>> {

    /** The tree root */
    private AvlNode<AnyType> root;

    /** Construct an empty AVL Tree */
    public AvlTree() {
        root = null;
    }

    /**
     * Inserts the specified element into the tree.
     * Duplicate elements are ignored.
     *
     * @param x the element to insert
     */
    public void insert(AnyType x) {
        root = insert(x, root);
    }

    /**
     * Removes the specified element from the tree, if it exists.
     *
     * @param x the element to remove
     */
    public void remove(AnyType x) {
        root = remove(x, root);
    }

    /**
     * Recursively removes an element from the given subtree.
     *
     * @param x the element to remove
     * @param t the root node of the subtree
     * @return the new root of the subtree
     */
    private AvlNode<AnyType> remove(AnyType x, AvlNode<AnyType> t) {
        if (t == null)
            return t;

        int compareResult = x.compareTo(t.element);

        if (compareResult < 0)
            t.left = remove(x, t.left);
        else if (compareResult > 0)
            t.right = remove(x, t.right);
        else if (t.left != null && t.right != null) {
            t.element = findMin(t.right).element;
            t.right = remove(t.element, t.right);
        } else
            t = (t.left != null) ? t.left : t.right;

        return balance(t);
    }

    /**
     * Finds and returns the smallest element in the tree.
     *
     * @return the smallest element
     * @throws UnderflowException if the tree is empty
     */
    public AnyType findMin() {
        if (isEmpty())
            throw new UnderflowException();
        return findMin(root).element;
    }

    /**
     * Finds and returns the largest element in the tree.
     *
     * @return the largest element
     * @throws UnderflowException if the tree is empty
     */
    public AnyType findMax() {
        if (isEmpty())
            throw new UnderflowException();
        return findMax(root).element;
    }

    /**
     * Determines whether the tree contains the specified element.
     *
     * @param x the element to search for
     * @return true if the element is found, false otherwise
     */
    public boolean contains(AnyType x) {
        return contains(x, root);
    }

    /** Make the tree logically empty. */
    public void makeEmpty() {
        root = null;
    }

    /**
     * Checks whether the tree is logically empty.
     *
     * @return true if the tree has no elements, false otherwise
     */
    public boolean isEmpty() {
        return root == null;
    }

    /** Print the tree contents in sorted order. */
    public void printTree() {
        if (isEmpty())
            System.out.println("Empty tree");
        else
            printTree(root);
    }

    private static final int ALLOWED_IMBALANCE = 1;

    /**
     * Balances the given subtree if it becomes unbalanced.
     *
     * @param t the root node of the subtree
     * @return the new root of the balanced subtree
     */
    private AvlNode<AnyType> balance(AvlNode<AnyType> t) {
        if (t == null)
            return t;

        if (height(t.left) - height(t.right) > ALLOWED_IMBALANCE)
            if (height(t.left.left) >= height(t.left.right))
                t = rotateWithLeftChild(t);
            else
                t = doubleWithLeftChild(t);
        else if (height(t.right) - height(t.left) > ALLOWED_IMBALANCE)
            if (height(t.right.right) >= height(t.right.left))
                t = rotateWithRightChild(t);
            else
                t = doubleWithRightChild(t);

        t.height = Math.max(height(t.left), height(t.right)) + 1;
        return t;
    }

    /**
     * Recursively inserts an element into the given subtree.
     *
     * @param x the element to insert
     * @param t the root node of the subtree
     * @return the new root of the subtree
     */
    private AvlNode<AnyType> insert(AnyType x, AvlNode<AnyType> t) {
        if (t == null)
            return new AvlNode<>(x, null, null);

        int compareResult = x.compareTo(t.element);

        if (compareResult < 0)
            t.left = insert(x, t.left);
        else if (compareResult > 0)
            t.right = insert(x, t.right);
        else
            ; // Duplicate, do nothing

        return balance(t);
    }

    /**
     * Finds the node with the smallest element in the given subtree.
     *
     * @param t the root node of the subtree
     * @return the node containing the smallest element, or null if empty
     */
    private AvlNode<AnyType> findMin(AvlNode<AnyType> t) {
        if (t == null)
            return null;
        else if (t.left == null)
            return t;
        return findMin(t.left);
    }

    /**
     * Finds the node with the largest element in the given subtree.
     *
     * @param t the root node of the subtree
     * @return the node containing the largest element, or null if empty
     */
    private AvlNode<AnyType> findMax(AvlNode<AnyType> t) {
        if (t == null)
            return null;
        else if (t.right == null)
            return t;
        return findMax(t.right);
    }

    /**
     * Checks whether the given subtree contains the specified element.
     *
     * @param x the element to search for
     * @param t the root node of the subtree
     * @return true if found, false otherwise
     */
    private boolean contains(AnyType x, AvlNode<AnyType> t) {
        if (t == null)
            return false;

        int compareResult = x.compareTo(t.element);

        if (compareResult < 0)
            return contains(x, t.left);
        else if (compareResult > 0)
            return contains(x, t.right);
        else
            return true;
    }

    /**
     * Prints the elements of the given subtree in sorted (inorder) order.
     *
     * @param t the root node of the subtree
     */
    private void printTree(AvlNode<AnyType> t) {
        if (t != null) {
            printTree(t.left);
            System.out.println(t.element);
            printTree(t.right);
        }
    }

    /**
     * Returns the height of the given node.
     *
     * @param t the node whose height to retrieve
     * @return the node’s height, or -1 if null
     */
    private int height(AvlNode<AnyType> t) {
        return t == null ? -1 : t.height;
    }

    /**
     * Performs a single rotation with the left child.
     *
     * @param k2 the unbalanced node
     * @return the new root after rotation
     */
    private AvlNode<AnyType> rotateWithLeftChild(AvlNode<AnyType> k2) {
        AvlNode<AnyType> k1 = k2.left;
        k2.left = k1.right;
        k1.right = k2;
        k2.height = Math.max(height(k2.left), height(k2.right)) + 1;
        k1.height = Math.max(height(k1.left), k2.height) + 1;
        return k1;
    }

    /**
     * Performs a single rotation with the right child.
     *
     * @param k1 the unbalanced node
     * @return the new root after rotation
     */
    private AvlNode<AnyType> rotateWithRightChild(AvlNode<AnyType> k1) {
        AvlNode<AnyType> k2 = k1.right;
        k1.right = k2.left;
        k2.left = k1;
        k1.height = Math.max(height(k1.left), height(k1.right)) + 1;
        k2.height = Math.max(height(k2.right), k1.height) + 1;
        return k2;
    }

    /**
     * Performs a double rotation: left child with its right child.
     *
     * @param k3 the unbalanced node
     * @return the new root after rotation
     */
    private AvlNode<AnyType> doubleWithLeftChild(AvlNode<AnyType> k3) {
        k3.left = rotateWithRightChild(k3.left);
        return rotateWithLeftChild(k3);
    }

    /**
     * Performs a double rotation: right child with its left child.
     *
     * @param k1 the unbalanced node
     * @return the new root after rotation
     */
    private AvlNode<AnyType> doubleWithRightChild(AvlNode<AnyType> k1) {
        k1.right = rotateWithLeftChild(k1.right);
        return rotateWithRightChild(k1);
    }

    /**
     * Represents a node in an AVL tree.
     * Each node stores an element, references to its left and right children,
     * and its height within the tree.
     *
     * @param <AnyType> the type of element stored in this node
     */
    private static class AvlNode<AnyType> {
        AnyType element;
        AvlNode<AnyType> left;
        AvlNode<AnyType> right;
        int height;

        AvlNode(AnyType theElement) {
            this(theElement, null, null);
        }

        AvlNode(AnyType theElement, AvlNode<AnyType> lt, AvlNode<AnyType> rt) {
            element = theElement;
            left = lt;
            right = rt;
            height = 0;
        }
    }

    /** For debugging */
    public void checkBalance() {
        checkBalance(root);
    }

    /**
     * Recursively checks the balance of the given AVL subtree.
     * Prints "OOPS!!" if an imbalance or incorrect height is detected.
     *
     * @param t the root node of the subtree
     * @return the height of the subtree
     */

    private int checkBalance(AvlNode<AnyType> t) {
        if (t == null)
            return -1;
        int hl = checkBalance(t.left);
        int hr = checkBalance(t.right);
        if (Math.abs(height(t.left) - height(t.right)) > 1 ||
                height(t.left) != hl || height(t.right) != hr)
            System.out.println("OOPS!!");
        return height(t);
    }
}
