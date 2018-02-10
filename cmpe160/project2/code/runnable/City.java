package runnable;

import adts.MyPQ;
import adts.MyStack;

/**
 * Represents a city which has a name, a garage of waggons and a garage of locomotives. 
 * 
 * @author Cemal Burak AYGÜN
 *
 */
public class City {	

	/**
	 * The name of the city.
	 */
	private String name;
	
	/**
	 * The garage of waggons.
	 */
	private MyStack<Waggon> waggonGarage;
	
	/**
	 * The garage of locomotives.
	 */
	private MyPQ<Locomotive> locomotiveGarage;
	
	/**
	 * Creates a city with the given name and initializes its garages. 
	 * 
	 * @param name	The name of the city.
	 */
	public City(String name){
		this.name = name;
		waggonGarage = new MyStack<Waggon>();
		locomotiveGarage = new MyPQ<Locomotive>();
	}
	
	/**
	 * Adds the given waggon into the waggon garage.
	 * 
	 * @param waggon	The waggon to be added into the waggon garage.
	 */
	public void addWaggon(Waggon waggon){
		waggonGarage.push(waggon);
	}
	
	/**
	 * Adds the given locomotive into the locomotive garage. 
	 * 
	 * @param locomotive	The locomotive to be added into the locomotive garage.
	 */
	public void addLocomotive(Locomotive locomotive){
		locomotiveGarage.offer(locomotive);
	}
	
	/**
	 * Returns the name of the city.
	 * 
	 * @return	The name of the city.
	 */
	
	public String getName(){
		return name;
	}
	
	
	/**
	 * Returns the waggon garage of the city.
	 * 
	 * @return	The waggon garage of the city.
	 */
	public MyStack<Waggon> getWaggonGarage() {
		return waggonGarage;
	}

	/**
	 * Returns the locomotive garage of the city.
	 * 
	 * @return The locomotive garage of the city.
	 */
	public MyPQ<Locomotive> getLocomotiveGarage() {
		return locomotiveGarage;
	}
	
}
