package adts;

import intfs.DoublyLinkedListIntf;

public class MyDLL<T> implements DoublyLinkedListIntf<T>{

	/**
	 * Abstraction of a node in the Doubly Linked List (DLL) ADT.
	 *
	 * Example Usage: A Node in an integer DLL has the following fields:
	 * 		int data;
	 * 		Node<Integer> next, prev;
	 * 
	 * @param <E> type of the data.
	 */
	public class Node<E> {
		E data;
		Node<E> next;
		Node<E> prev;

		public Node(E data_) {
			data = data_;
		}
	}

	/**
	 *	The first node of the list.
	 *	NOTE: Made public for testing purposes.
	 */
	public Node<T> first;

	// CHANGES START BELOW THIS LINE
	
	/**
	 * The number of elements in this Doubly LinkedList which is initially zero.
	 */
	private int size = 0;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(T item) {
		if( first == null ){
			first = new Node<T>(item);
		}else{
			Node<T> newNode = new Node<T>(item);
			Node<T> current = first;
			while( current.next != null ){
				current = current.next;
			}
			current.next = newNode;
			newNode.prev = current;
 		}
		size++;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(T item, int index) {
		if( index == size ){
			add(item);
			return true;
		}
		if( index == 0 ){
			Node<T> newNode = new Node<T>(item);
			newNode.next = first;
			first.prev = newNode;
			first = newNode;
			size++;
			return true;
		}
		Node<T> current = getNode(index).prev;
		Node<T> newNode = new Node<T>(item);
		newNode.next = current.next;
		current.next.prev = newNode;
		current.next = newNode;
		newNode.prev = current;
		size++;
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public T remove(int index) {
		if( size == 0){
			throw new IndexOutOfBoundsException();
		}
		Node<T> current;
		if( index == 0 ){
			current = first;
			if( size == 1 ){
				first = null;
			}else{			
				first = first.next;
				first.prev = null;
			}			
		}else{
			current = getNode(index);
			if( index == size-1 ){
				current.prev.next = null;
			}else{
				current.prev.next = current.next;
				current.prev.next.prev = current.prev;
			}
		}
		size--;
		return current.data;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public T get(int index) {
		return getNode(index).data;
	}
	
	/**
	 * Returns the number of elements in this Doubly LinkedList.
	 * 
	 * @return	The number of elements in this Doubly LinkedList.
	 */
	public int size(){
		return size;
	}
	
	/**
	 * Returns the node of this Doubly LinkedList at the given index.
	 * 
	 * @param index		The index of the node.
	 * @return			The node at the given index.
	 * @exception IndexOutOfBoundsException if the index is less than 0 or greater than or equal to <code>size</code>.
	 */
	private Node<T> getNode(int index){
		if( index >= 0 && index < size ){
			Node<T> current = first;
			for( int i = 0 ; i < index ; i++){
				current = current.next;
			}
			return current;
		}else{
			throw new IndexOutOfBoundsException();
		}
		
	}

	// CHANGES END ABOVE THIS LINE

}
