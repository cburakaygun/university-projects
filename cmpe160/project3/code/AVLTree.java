import java.util.ArrayList;

public class AVLTree<T extends Comparable<T>> implements AVLTreeInterface<T> {

	public Node<T> root;

	/**
	 * Basic storage units in a tree. Each Node object has a left and right
	 * children fields.
	 * 
	 * If a node does not have a left and/or right child, its right and/or left
	 * child is null.
	 * 
	 */
	private class Node<E> {
		private E data;
		private Node<E> left, right; // left and right subtrees

		public Node(E data) {
			this.data = data;
		}
	}

	// CHANGES START BELOW THIS LINE
	
	
	/**
	 * The total number of nodes in the tree
	 */
	private int numberOfNodes = 0;
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return root == null;
	}
			

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return numberOfNodes;
	}
			

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(T element) {
		return findNode(element) != null;	// If there are not any nodes with given element, findNode returns null.
	}
		

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insert(T element) {
		if( isEmpty() ){
			root = new Node<T>(element);
			numberOfNodes++;
		}
		else if( insertAndBalance(element, root) ){
			root = balanceNode(root);	// If a new node has been added into the tree, re-balance the root of the tree.
		}		
	}
	
			
	/**
	 * Adds a new node with the given <code>element</code> into the tree if there is not already such a node
	 * and re-balances the nodes between the root and the newly created node recursively.
	 * 
	 * @param element	The element to be added into the tree as a node.
	 * @param root		The root node of the tree 
	 * @return			If the tree has already such an <code>element</code>, false; otherwise, true.
	 */
	private boolean insertAndBalance(T element, Node<T> root) {
		
		if( element.compareTo(root.data) < 0 ){
			
			if( root.left == null ){
				root.left = new Node<T>(element);
				numberOfNodes++;
				return true;
			}else{
				if( insertAndBalance(element, root.left) ){
					root.left = balanceNode(root.left);		// Re-balances the left child of the "parameter node".
					return true;
				}else{
					return false;
				}
			}
			
		}else if( element.compareTo(root.data) > 0 ){
			
			if( root.right == null ){
				root.right = new Node<T>(element);
				numberOfNodes++;
				return true;
			}else{
				if( insertAndBalance(element, root.right) ){
					root.right = balanceNode(root.right);	// Re-balances the right child of the "parameter node".
					return true;
				}else{
					return false;
				}				
			}
			
		}
		
		return false;   // if the element == node.data (i.e. there already exists a node with the given element in the tree)
		
	}
	
			
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(T element) {
		if( isEmpty() ){
			return;
		}
		if( element.compareTo(root.data) == 0 ){	// If the node to be deleted is the root
			if( root.right != null ){	// If the old root has a right child
				Node<T> successor = findTheLeftMostChildAndBalance(root.right);		// The new root
				successor.left = root.left;		// Makes the left child of the old root the left child of the successor (the new root).
				if( root.right != successor ){		// i.e. if the right child of the old root has a left child
					successor.right = root.right;	// Makes the right child of the old root the right child of the successor (the new root).
					successor.right = balanceNode(successor.right);		// Re-balances the right child of the successor.
				}
				root = balanceNode(successor);	// Re-balances the successor and makes it the new root of the tree.
			}else{		// If the old root has no right child
				root = root.left;		// Makes the left child of the old root the new root of the tree.
			}
			numberOfNodes--;
			return;
		}
		if( deleteAndBalance(element, root) ){	// If the node to be deleted is different from the root.
			root = balanceNode(root);	// Re-balances the root of the tree.
		}
	}


	/**
	 * Deletes the node with the given <code>element</code> if there is such a node in the tree by replacing it
	 * with its successor and re-balances the nodes between the root and the deleted node recursively.
	 * 
	 * @param element	The element of the node to be deleted from the tree.
	 * @param root		The root node of the tree.
	 * @return			If the tree has not such an <code>element</code>, false; otherwise, true.
	 */
	private boolean deleteAndBalance(T element, Node<T> root){		
		
		if( element.compareTo(root.data) < 0 ){		// If the element is smaller than the "parameter node"
			
			if( root.left == null ){
				return false;	// If the element is smaller than the "parameter node" but the "parameter node" has no left child, then there is no such an element in the tree.
			}
			else if( element.compareTo(root.left.data) == 0 ){	// If the node to be deleted is the left child of the "parameter node"
				Node<T> nodeToBeDeleted = root.left;
				if( nodeToBeDeleted.right != null ){	// If the node to be deleted has a right child
					Node<T> successor = findTheLeftMostChildAndBalance(nodeToBeDeleted.right);	// The node which takes the place of the node which will be deleted.
					successor.left = nodeToBeDeleted.left;	// Makes the left child of the node to be deleted the left child of the successor.
					if( nodeToBeDeleted.right != successor ){	// i.e. if the right child of the node to be deleted has a left child
						successor.right = nodeToBeDeleted.right;	// Makes the right child of the node to be deleted the right child of the successor.
						successor.right = balanceNode(successor.right);	// Re-balances the right child of the successor.
					}
					root.left = balanceNode(successor);	// Re-balances the successor and makes it the left child of the "parameter node".
				}
				else{	// If the node to be deleted has no right child
					root.left = nodeToBeDeleted.left;	// Makes the left child of the node to be deleted the left child of the "parameter node".
				}
				numberOfNodes--;
				return true;
			}
			else{	// if the node to be deleted is smaller than the left child of the "parameter node"
				if( deleteAndBalance(element, root.left) ){
					root.left = balanceNode(root.left);	// Re-balances the left child of the "parameter node".
					return true;
				}
				else{
					return false;
				}
			}
			
		}
		else{		// If the element is greater than the "parameter node"
			
			if( root.right == null ){
				return false;	// If the element is greater than the "parameter node" but the "parameter node" has no right child, then there is no such an element in the tree.
			}
			else if( element.compareTo(root.right.data) == 0 ){	// If the node to be deleted is the right child of the "parameter node"
				Node<T> nodeToBeDeleted = root.right;
				if( nodeToBeDeleted.right != null ){	// If the node to be deleted has a right child
					Node<T> successor = findTheLeftMostChildAndBalance(nodeToBeDeleted.right);	// The node which takes the place of the node which will be deleted.
					successor.left = nodeToBeDeleted.left;	// Makes the left child of the node to be deleted the left child of the successor.
					if( nodeToBeDeleted.right != successor ){	// i.e. if the right child of the node to be deleted has a left child
						successor.right = nodeToBeDeleted.right;	// Makes the right child of the node to be deleted the right child of the successor.
						successor.right = balanceNode(successor.right);	// Re-balances the right child of the successor.
					}
					root.right = balanceNode(successor);	// Re-balances the successor and makes it the right child of the "parameter node".
				}
				else{	// If the node to be deleted has no right child
					root.right = nodeToBeDeleted.left;	// Makes the left child of the node to be deleted the right child of the "parameter node".
				}
				numberOfNodes--;
				return true;
			}
			else{	// if the node to be deleted is greater than the right child of the "parameter node"
				if( deleteAndBalance(element, root.right) ){
					root.right = balanceNode(root.right);	// Re-balances the right child of the "parameter node".
					return true;
				}
				else{
					return false;
				}				
			}
			
		}
		
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public int height() {
		return heightOfNode(root);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<T> inOrderTraversal() {
		ArrayList<T> result = new ArrayList<T>();
		inOrderTraverse(root, result);
		return result;
	}
	
	
	/**
	 * Starting from the given <code>node</code>, traverses the tree "in order" recursively 
	 * and adds the <code>data</code> field of the nodes into the given <code>ArrayList</code>.
	 * 
	 * @param node			The node whose left subtree, itself and right subtree will be added into given ArrayList
	 * @param arrayList		The ArrayList into which the data field of <code>node</code> and its left and right subtrees will be added.
	 */
	private void inOrderTraverse(Node<T> node, ArrayList<T> arrayList){
		if( node != null ){
			inOrderTraverse(node.left, arrayList);
			arrayList.add(node.data);
			inOrderTraverse(node.right, arrayList);
		}
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<T> bfTraverse() {
		ArrayList<T> result = new ArrayList<T>();
		if( root == null ){
			return result;
		}
		ArrayList<Node<T>> temp = new ArrayList<Node<T>>();
		temp.add(root);
		while( !temp.isEmpty() ){
			Node<T> current = temp.remove(0);
			result.add(current.data);
			if( current.left != null ){
				temp.add(current.left);
			}
			if( current.right != null ){
				temp.add(current.right);
			}
		}
		return result;
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean areCousins(T element1, T element2) {
		if( element1 == element2 ){
			return false;
		}
		
		Node<T> parent1 = findParent(element1);
		Node<T> parent2 = findParent(element2);
		
		/* 
		 * If the parent of a node is null, that means either the node is the root (the root has no cousin)
		 * or there is no such a node in the tree (hence it cannot be a cousin of another node).
		 * 
		 * None of root's children can be a cousin of any node.
		 */
		if( parent1 == null || parent2 == null || parent1 == root || parent2 == root || parent1 == parent2 ){
			return false;
		}
		
		/* 
		 * If we are here, it means that both of the nodes have a parent and the parents are not the same.
		 * 
		 * If the grandparents of the nodes are the same, then the nodes are cousins.
		 */
		return findParent(parent1.data) == findParent(parent2.data);
		
	}
	
	
	/**
	 * Finds the parent of the node which has the given element in its <code>data</code> field.
	 * <p>
	 * If the node is the root or there is no such a node with the given parameter, returns null.
	 * 
	 * @param element	The element of the node whose parent to be found.
	 * @return			The parent of the node which has the given element in its <code>data</code> field. 
	 */
	private Node<T> findParent(T element){
		Node<T> current = root;
		Node<T> parent = null;		// The parent of the root is null.
		while( current != null ){
			if( element.compareTo(current.data) == 0 ){
				return parent;
			}
			if( element.compareTo(current.data) < 0 ){
				parent = current;
				current = current.left;
			}else{
				parent = current;
				current = current.right;
			}
		}
		return null;	// If we are here, it means that there is no such a node with the given parameter in the tree.
	}
			

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int numElementsInRange(T lower, T upper) {
		if( isEmpty() || lower.compareTo(upper) >= 0 ){
			return 0;
		}
		
		ArrayList<T> inOrderArrayList = inOrderTraversal();
		int arraySize = inOrderArrayList.size();
		
		if( lower.compareTo(inOrderArrayList.get(arraySize-1)) >= 0 || upper.compareTo(inOrderArrayList.get(0)) <= 0 ){
			return 0;
		}
		
		int lowerIndex = -1;
		for( int i = 0; i < arraySize ; i++ ){
			if( inOrderArrayList.get(i).compareTo(lower) > 0 ){
				lowerIndex = i;
				break;
			}
		}
		
		int upperIndex = -1;		
		for( int i = arraySize-1 ; i >= 0 ; i-- ){
			if( inOrderArrayList.get(i).compareTo(upper) < 0 ){
				upperIndex = i;
				break;
			}
		}
		
		return upperIndex - lowerIndex + 1;
	}
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int balanceFactor(T data) {
		Node<T> current = findNode(data);
		return balanceFactor(current);
	}
	
	
	/**
	 * Calculates the balance factor of the given <code>node</code> recursively.
	 * 
	 * @param node	The node whose balance factor to be calculated.
	 * @return		The balance factor of the given <code>node</code>. If the <code>node</code> is null, 0.
	 */
	private int balanceFactor(Node<T> node){
		if( node == null ){
			return 0;
		}
		return heightOfNode(node.left) - heightOfNode(node.right);
	}
	
	
	/**
	 * Finds the node which has the given <code>element</code> in its <code>data</code> field.
	 * <p>
	 * If there is no such a node in the tree, returns null.
	 * 
	 * @param element	The element of the node to be found.
	 * @return			The node which has the given <code>element</code> in its <code>data</code> field.
	 */
	private Node<T> findNode(T element){
		if( isEmpty() ){
			return null;
		}
		
		Node<T> current = root;
		while( current != null ){
			if( element.compareTo(current.data) == 0 ){
				return current;
			}
			if( element.compareTo(current.data) < 0 ){
				current = current.left; 
			}else{
				current = current.right;
			}
		}
		
		return null;		// If we are here, it means that there is no such a node in the tree.
	}
	
	
	/**
	 * Balances the node if necessary using some rotations and returns the new balanced node.
	 * 
	 * @param node	The node to be balanced
	 * @return		The new balanced node.	
	 */
	private Node<T> balanceNode(Node<T> node){		
		int balanceFactor = balanceFactor(node);
		
		if( -1 <= balanceFactor && balanceFactor <= 1 ){
			return node;	// If the node is already balanced, returns itself without any change.
		}
		
		if( balanceFactor(node) == 2 ){
			if( balanceFactor(node.left) == -1 ){
				return leftRightRotation(node);
			}else{
				return rightRotation(node);
			}
		}else{
			if( balanceFactor(node.right) == 1 ){
				return rightLeftRotation(node);
			}else{
				return leftRotation(node);
			}
		}		
	}
	
	
	/**
	 * Performs a right rotation on the given <code>node</code>.
	 * 
	 * @param node	The root of the (sub)tree on which a right rotation will be performed.
	 * @return		The new root of the right-rotated (sub)tree.
	 */
	private Node<T> rightRotation(Node<T> node){
		Node<T> newParent = node.left;
		node.left = node.left.right;
		newParent.right = node;
		return newParent;
	}
	
	
	/**
	 * Performs a double rotation (left-right) on the given <code>node</code>.
	 * First, performs a left rotation on the left child of the given <code>node</code>.
	 * Then, performs a right rotation on the given <code>node</code>.
	 * 
	 * @param node	The root of the (sub)tree on which a double (left-right) rotation will be performed.
	 * @return		The new root of the double-rotated (left-right) (sub)tree.
	 */
	private Node<T> leftRightRotation(Node<T> node){
		node.left = leftRotation(node.left);
		return rightRotation(node);
	}
	
	
	/**
	 * Performs a left rotation on the given <code>node</code>.
	 * 
	 * @param node	The root of the (sub)tree on which a left rotation will be performed.
	 * @return		The new root of the right-rotated (sub)tree.
	 */
	private Node<T> leftRotation(Node<T> node){
		Node<T> newParent = node.right;
		node.right = node.right.left;
		newParent.left = node;
		return newParent;
	}
	
	
	/**
	 * Performs a double rotation (right-left) on the given <code>node</code>.
	 * First, performs a right rotation on the right child of the given <code>node</code>.
	 * Then, performs a left rotation on the given <code>node</code>.
	 * 
	 * @param node	The root of the (sub)tree on which a double (right-left) rotation will be performed.
	 * @return		The new root of the double-rotated (right-left) (sub)tree.
	 */
	private Node<T> rightLeftRotation(Node<T> node){
		node.right = rightRotation(node.right);
		return leftRotation(node);
	}
	

	/**
	 * Finds the left-most child of the given <code>node</code> 
	 * and re-balances the nodes between the given <code>node</code> and its left-most child recursively.
	 * (The given <code>node</code> and its left-most child are not included in re-balancing process.)
	 * <p>
	 * If the given <code>node</code> has no left child, returns the node itself without any change.
	 * <p>
	 * Makes the right child of the "result node" the new left child of the parent of the "result node". 
	 * 
	 * @param node	The node whose left-most child to be found.
	 * @return		The left-most child of the given node
	 */
	private Node<T> findTheLeftMostChildAndBalance(Node<T> node){		
		if( node.left == null ){
			return node;		// If the node has no left child, returns the node itself.
		}
		else if( node.left.left == null ){	// If the left child of the "node" has no left child,
			Node<T> result = node.left;	// the left child of the "node" is the successor.
			node.left = node.left.right;	// The right child of the successor becomes the left child of the "node".
			return result;
		}
		else{	// If the left child of the "node" has a left child,
			Node<T> result = findTheLeftMostChildAndBalance( node.left );		// finds the most-left child of the left child of the "node".
			node.left = balanceNode(node.left);		// Re-balances the left child of the "node".
			return result;	
		}
	}
	
	
	/**
	 * Calculates the height of the given <code>node</code> recursively.
	 * 
	 * @param node	The node whose height to be calculated.
	 * @return		The height of the given <code>node</code>.
	 */
	private int heightOfNode(Node<T> node){
		if( node == null ){
			return -1;
		}
		if( node.left == null && node.right == null ){	// If the node is a leaf.
			return 0;
		}
		return Math.max(heightOfNode(node.left), heightOfNode(node.right)) + 1;
	}


	// CHANGES END ABOVE THIS LINE	
}
