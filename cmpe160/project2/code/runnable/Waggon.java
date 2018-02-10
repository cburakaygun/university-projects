package runnable;

/**
 * Represents a waggon which has a name.
 * 
 * @author Cemal Burak AYGÜN
 *
 */
public class Waggon {

	/**
	 * The name of the waggon.
	 */
	private String name;
	
	/**
	 * Creates a waggon with the given name.
	 * 
	 * @param name	The name of the waggon.
	 */
	public Waggon(String name){
		this.name = name;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(){
		return name;
	}
}
