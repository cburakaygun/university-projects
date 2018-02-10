package runnable;

/**
 * Represents a locomotive which has a name and an engine power.
 * 
 * @author Cemal Burak AYGÜN
 *
 */
public class Locomotive extends Waggon implements Comparable<Locomotive>{

	/**
	 * The engine power of the locomotive.
	 */
	private double enginePower;
	
	/**
	 * Creates a locomotive with the given name and the given engine power.
	 * 
	 * @param name			The name of the locomotive.
	 * @param enginePower	The engine power of the locomotive.
	 */
	public Locomotive(String name, double enginePower){
		super(name);
		this.enginePower = enginePower;
	}

	/**
	 * Returns the engine power of the locomotive.
	 * 
	 * @return	The engine power of the locomotive.
	 */
	public double getEnginePower() {
		return enginePower;
	}
	
	/**
	 * Compares this locomotive and the other locomotive according to their engine powers.
	 * 
	 * @return	-1 if this engine power is less than other's; 1 if this engine power is greater than other's; 0 if they are equal.
	 */
	@Override
	public int compareTo(Locomotive other) {
		if( enginePower < other.getEnginePower() ){
			return -1;
		}else if( enginePower > other.getEnginePower() ){
			return 1;
		}else{
			return 0;
		}
	}	
	
}
