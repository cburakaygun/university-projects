package adts;

import java.util.ArrayList;

import intfs.StackIntf;

public class MyStack<T> implements StackIntf<T> {
	
	/**
	 * data added to the stack must be stored in <code>list</code>
	 */
	ArrayList<T> list = new ArrayList<T>();
	
	// CHANGES START BELOW THIS LINE
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public T push(T item) {
		list.add(item);
		return item;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public T pop() {
		if( isEmpty() ){
			return null;
		}		
		T result = list.get(list.size()-1);
		list.remove(list.size()-1);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T peek() {
		if( isEmpty() ){
			return null;
		}		
		return list.get(list.size()-1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return list.size() == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int count() {
		return list.size();
	}
	
	/**
	 * Adds every element (the name of the waggon) of the stack to a String using <code>pop()</code> method.
	 * (Hence, also deletes every element from the stack.)
	 * <p>
	 * Only one element is written in each line of the String.
	 * <p>
	 * The element which is at the end of the <code>list</code> is written in the first line and 
	 * the element which is at the front of the <code>list</code> is written in the last line.
	 * 
	 * @return	The String in which the elements (names of the waggons) of the stack is written.
	 */
	public String getElements(){
		String result = "";
		while( !isEmpty() ){
			result += "\n" + pop();
		}
		return result;
	}
	
	// CHANGES END ABOVE THIS LINE
}
