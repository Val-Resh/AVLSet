import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Set of unique values. Stores only non-repeated elements.
 * @param <E> type of element
 * @author Val Resh
 */
public class AVLSet<E extends Comparable<E>> {
    private Node<E> root;
    private int numberOfElements;

    /**
     * Node data structure that holds each node of the tree.
     * @param <E> element type.
     */
    private static class Node<E> {
        private E element;
        private int height;
        private Node<E> right;
        private Node<E> left;

        private Node(E element){
            this.element = element; right = null; left = null;
            height = 1;
        }
    }

    /*
    Empty constructor.
     */
    public AVLSet(){
        root = null;
        numberOfElements = 0;
    }

    /*
    Constructor that takes a root node.
     */
    public AVLSet(E root){
        this.root = new Node<>(root);
        numberOfElements = 1;
    }

    /**
     * Add an element to the set. Stores non-repeated elements only.
     * If an element is already in the set, then it will not be added again.
     * @param element - the element E to add to set.
     */
    public void add(E element){
        if(isEmpty()) {
            root = new Node<>(element);
            numberOfElements++;
        }
        else root = recursiveAddAndBalance(root, element);
    }

    /**
     * Searches an element in the set. If it is present, it returns the element.
     * Otherwise, returns null.
     * @param element element to search for
     * @return element if found, null otherwise.
     */
    public E find(E element) {
        Node<E> node = root;
        while(node != null){
            if(element.compareTo(node.element) < 0)
                node = node.left;
            else if(element.compareTo(node.element) > 0)
                node = node.right;
            else return node.element;
        }
        return null;
    }

    /**
     * Removes an element from the set, if said element exists.
     * @param element element to delete.
     */
    public void remove(E element){
        root = deleteRecursively(root, element);
    }

    /**
     * Get the max element in the set.
     * @return max element.
     * @throws RuntimeException cannot be called on an empty set.
     */
    public E max(){
        if(isEmpty()){
            throw new RuntimeException("Cannot get max on an empty set.");
        }
        return recursiveMax(root);
    }

    /**
     * Get the max element of the union of this set with a second set.
     * @param set2 second set
     * @return max element
     * @throws RuntimeException throws exception if either or both sets are empty.
     */
    public E max(AVLSet<E> set2){
        E setOneMax = this.max();
        E setTwoMax = set2.max();
        return (setOneMax.compareTo(setTwoMax) > 0)
                ? setOneMax : setTwoMax;
    }

    /**
     * Get the min element of the union of this set with a second set.
     * @param set2 second set
     * @return min element
     * @throws RuntimeException throws exception if either or both sets are empty.
     */
    public E min(AVLSet<E> set2){
        E setOneMin = this.min();
        E setTwoMin = set2.min();
        return (setOneMin.compareTo(setTwoMin) > 0)
                ? setTwoMin : setOneMin;
    }

    /**
     * Get the min element in the set.
     * @return min element.
     * @throws RuntimeException cannot be called on an empty set.
     */
    public E min(){
        if(isEmpty()){
            throw new RuntimeException("Cannot get min on an empty set.");
        }
        return recursiveMin(root);
    }

    /**
     * Performs a union on this set with a second set.
     * @param set set to union with this set.
     * @return returns a new set union of A and B.
     */
    public AVLSet<E> union(AVLSet<E> set){
        AVLSet<E> union = new AVLSet<>();
        inorderIterativeUnion(union, this);
        inorderIterativeUnion(union, set);
        return union;
    }

    /**
     * Performs an intersection operation on this set with another set.
     * Returns a new set that contains the intersection elements of both.
     * @param set set to intersect with
     * @return a new set with intersection elements of both.
     */
    public AVLSet<E> intersection(AVLSet<E> set){
        AVLSet<E> intersection = new AVLSet<>();

        Deque<Node<E>> stack = new ArrayDeque<>();

        Node<E> current = set.root;
        while(current != null || stack.size() > 0){
            while(current != null){
                stack.push(current);
                current = current.left;
            }
            current = stack.pop();
            E element = current.element;
            if(this.find(element) != null) intersection.add(element);
            current = current.right;
        }

        return intersection;
    }

    /**
     * Returns a new set which contains the relative complement of parameter set in this set.
     * Sometimes known as subtraction of sets. If this set is A and parameter set is B.
     * Then the new set will be A - B.
     * @param set set to subtract
     * @return new set that contains the relative complement
     */
    public AVLSet<E> relativeComplement(AVLSet<E> set){
        AVLSet<E> complement = new AVLSet<>();
        inorderIterativeUnion(complement, this);
        Deque<Node<E>> stack = new ArrayDeque<>();

        Node<E> current = set.root;
        while(current != null || stack.size() > 0){
            while(current != null){
                stack.push(current);
                current = current.left;
            }
            current = stack.pop();
            E element = current.element;
            if(complement.find(element) != null) complement.remove(element);
            current = current.right;
        }

        return complement;
    }

    /**
     * Returns a new set which contains the symmetric difference of the set it is called on, and the parameter set.
     * @param set the second set
     * @return new set with symmetric difference of A and B
     */
    public AVLSet<E> symmetricDifference(AVLSet<E> set) {
        AVLSet<E> symmetric = new AVLSet<>();
        inorderIterativeUnion(symmetric, this);
        inorderIterativeUnion(symmetric, set);

        Deque<Node<E>> stack = new ArrayDeque<>();

        Node<E> current = set.root;
        while(current != null || stack.size() > 0){
            while(current != null){
                stack.push(current);
                current = current.left;
            }
            current = stack.pop();
            E element = current.element;
            if(this.find(element) != null && set.find(element) != null) symmetric.remove(element);
            current = current.right;
        }

        return symmetric;
    }

    /**
     * Returns true if the set is empty, false otherwise.
     * @return boolean
     */
    public boolean isEmpty(){
        return numberOfElements == 0;
    }

    /**
     * Returns the size of the set. Size is defined by the number of elements in set.
     * @return number of elements in set.
     */
    public int size() { return numberOfElements; }

    /**
     * Recursive helper function to delete a node from the tree.
     * @param node node for each recursive call.
     * @param element constant element to delete
     * @return nodes back up the tree, balancing them out.
     */
    private Node<E> deleteRecursively(Node<E> node, E element){
        if(isEmpty()) return null;

        if(element.compareTo(node.element) < 0)
            node.left = deleteRecursively(node.left, element);
        else if(element.compareTo(node.element) > 0)
            node.right = deleteRecursively(node.right, element);

        else {
            numberOfElements--;
            if(node.left == null) return node.right;
            else if(node.right == null) return node.left;
            else {
                node.element = recursiveMax(node.left);
                node.left = deleteRecursively(node.left, node.element);
            }
        }

        node.height = getUpdatedHeight(node.left, node.right);
        return balanceNode(node);
    }

    /**
     * Helper function to get the max element in tree or subtree.
     * @param node parent node.
     * @return max element in the tree/subtree.
     */
    private E recursiveMax(Node<E> node){
        if(node.right != null)
            return recursiveMax(node.right);
        return node.element;
    }

    /**
     * Helper function to get the min element in tree of subtree.
     * @param node parent node.
     * @return min element in the tree/subtree.
     */
    private E recursiveMin(Node<E> node){
        if(node.left != null)
            return recursiveMin(node.left);
        return node.element;
    }
    /**
     * Performs the necessary rotations to balance the node.
     * @param node node to check
     * @return balanced node
     */
    private Node<E> balanceNode(Node<E> node){
        int balanceFactor = getBalanceFactor(node);

        if(balanceFactor > 1){
            Node<E> right = node.right;
            if(getBalanceFactor(right) < 0)
                node.right = rightRotation(right);
            return leftRotation(node);
        }

        if(balanceFactor < -1){
            Node<E> left = node.left;
            if(getBalanceFactor(left) > 0)
                node.left = leftRotation(left);
            return rightRotation(node);
        }

        return node;
    }

    /**
     * Helper function to inorder iterate through a tree, then add each node's element to the set.
     * @param mutate set to store elements
     * @param union set to retrieve elements
     */
    private void inorderIterativeUnion(AVLSet<E> mutate, AVLSet<E> union){
        Deque<Node<E>> stack = new ArrayDeque<>();

        Node<E> current = union.root;
        while(current != null || stack.size() > 0){
            while(current != null){
                stack.push(current);
                current = current.left;
            }
            current = stack.pop();
            mutate.add(current.element);
            current = current.right;
        }
    }

    /**
     * Helper function to add an element to the tree via recursion.
     * It traverses down the tree recursively to insert the element, then upwards to the root to balance the tree.
     * @param node node traversed
     * @param element element to add
     * @return each node traversing up the tree recursively, the returned node is balanced.
     */
    private Node<E> recursiveAddAndBalance(Node<E> node, E element){
        if(node == null){
            numberOfElements++;
            return new Node<>(element);
        }

        if(element.compareTo(node.element) > 0){
            node.right = recursiveAddAndBalance(node.right, element);
        }
        else if (element.compareTo(node.element) < 0){
            node.left = recursiveAddAndBalance(node.left, element);
        }
        else{
            return node;
        }

        node.height = getUpdatedHeight(node.left, node.right);
        return balanceNode(node);
    }

    /**
     * Performs a right rotation on the unbalanced node.
     * @param N node
     * @return node after rotation.
     */
    private Node<E> rightRotation(Node<E> N){
        Node<E> leftNode = N.left;
        Node<E> leftNodeRight = leftNode.right;
        leftNode.right = N;
        N.left = leftNodeRight;
        N.height = getUpdatedHeight(N.left, N.right);
        leftNode.height = getUpdatedHeight(leftNode.left, leftNode.right);
        return leftNode;
    }

    /**
     * Performs a left rotation on the unbalanced node.
     * @param N node
     * @return node after rotation.
     */
    private Node<E> leftRotation(Node<E> N){
        Node<E> rightNode = N.right;
        Node<E> rightNodeLeft = rightNode.left;
        rightNode.left = N;
        N.right = rightNodeLeft;
        N.height = getUpdatedHeight(N.left, N.right);
        rightNode.height = getUpdatedHeight(rightNode.left, rightNode.right);
        return rightNode;
    }



    /**
     * Returns the updated height of a node.
     */
    private int getUpdatedHeight(Node<E> left, Node<E> right){
        int leftHeight = (left == null) ? 0 : left.height;
        int rightHeight = (right == null) ? 0 : right.height;
        return (leftHeight > rightHeight) ? leftHeight+1 : rightHeight+1;
    }

    /**
     * Returns the balance factor of a node.
     * Balanced nodes are within the range of {-1,0.1}
     * Balance Factor is calculated as height(rightSubtree(N))-height(leftSubtree(N)).
     * @param N node
     * @return balance factor as an int.
     */
    private int getBalanceFactor(Node<E> N){
        if(N == null) return 0;
        int left = (N.left == null) ? 0 : N.left.height;
        int right = (N.right == null) ? 0 : N.right.height;
        return right - left;
    }
}
