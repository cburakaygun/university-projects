package adts;

import intfs.PriorityQueueIntf;

import java.util.LinkedList;

public class MyPQ<T extends Comparable<T>> implements PriorityQueueIntf<T> {


	/**
	 * data added to the stack must be stored in <code>list</code>
	 */
	LinkedList<T> list = new LinkedList<T>();

	// CHANGES START BELOW THIS LINE
	
	/**
	 * Adds an item into the queue in such a way that they are sorted from greater (engine power) to lower.
	 * 
	 * @param item Item to be added.
	 * @return true if successful; false otherwise.
	 */
	@Override
	public boolean offer(T item) {
		for( int i = 0 ; i < count() ; i++ ){
			T current = list.get(i);
			if( item.compareTo(current) > 0 ){
				list.add(i, item);
				return true;
			}
		}
		return list.add(item);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public T poll() {
		return list.poll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T peek() {
		return list.peek();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int count() {
		return list.size();
	}
	
	/**
	 * Adds every element (the name of the locomotive) of the queue to a String using <code>poll()</code> method.
	 * (Hence, also deletes all the elements from the queue.)
	 * <p>
	 * Only one element is written in each line of the String.
	 * <p>
	 * The element which is at the front of the <code>list</code> is written in the first line and 
	 * the element which is at the end of the <code>list</code> is written in the last line.
	 * 
	 * @return	The String in which the elements (names of the locomotives) of the queue is written.
	 */
	public String getElements(){
		String result = "";
		while( count() != 0 ){
			result += "\n" + poll();
		}
		return result;
	}
	
	// CHANGES END ABOVE THIS LINE
	
}
