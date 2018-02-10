package runnable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import adts.MyDLL;

/**
 * Reads input files, simulates transportation scenarios and creates an output file.
 * 
 * @author Cemal Burak AYGÜN
 *
 */
public class Main {
	
	/**
	 * Calls <code>runSimulation</code> method with the names of the input files.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
			runSimulation("data/dests.txt", "data/waggons.txt", "data/locs.txt", "data/missions.txt", "data/result.txt");
	}

	/**
	 * Calls some other methods to create cities, deliver waggons and locomotives to the cities,
	 * complete the missions and write an output file.
	 * 
	 * @param destsFileName		The name of the file which stores the names of the cities.
	 * @param waggonsFileName	The name of the file which stores the names and cities of the waggons.
	 * @param locsFileName		The name of the file which stores the names, cities and engine powers of the locomotives.
	 * @param missionsFileName	The name of the file which stores the missions.
	 * @param outputFileName	The name of the file which stores the information about the final conditions of the cities and their garages.
	 */
	public static void runSimulation(String destsFileName, String waggonsFileName, 
			String locsFileName, String missionsFileName, String outputFileName) {
		// your code goes below	
		createCities(destsFileName);
		deliverWaggons(waggonsFileName);
		deliverLocomotives(locsFileName);
		completeMissions(missionsFileName);
		writeOutput(outputFileName);
	}
	
	/**
	 * Reading the input file, creates cities and adds them to the <code>cities</code> ArrayList.
	 * <p>
	 * Assumes that the input file is always valid.
	 * 
	 * @param destsFileName	The name of the file which stores the names of the cities.
	 */
	private static void createCities(String destsFileName){
		try{
			Scanner input = new Scanner(new File(destsFileName));
			while( input.hasNextLine() ){
				cities.add(new City(input.nextLine()));
			}
			input.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
			}
	}
	
	/**
	 * Reading the input file, creates waggons and adds them to the garages in the corresponding cities.
	 * <p>
	 * Assumes that the input file is always valid. 
	 * 
	 * @param waggonsFileName	The name of the file which stores the names and cities of the waggons.
	 */
	private static void deliverWaggons(String waggonsFileName){
		try{
			Scanner input = new Scanner(new File(waggonsFileName));
			while( input.hasNext() ){
				String waggonName = input.next();
				String cityName = input.next();
				findCity(cityName).addWaggon(new Waggon(waggonName));
			}
			input.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
			}
	}
	
	/**
	 * Reading the input file, creates locomotives and adds them to the garages in the corresponding cities.
	 * <p>
	 * Assumes that the input file is always valid. 
	 * 
	 * @param locsFileName	The name of the file which stores the names, cities and engine powers of the locomotives.
	 */
	private static void deliverLocomotives(String locsFileName){
		try{
			Scanner input = new Scanner(new File(locsFileName));
			while( input.hasNext() ){
				String locomotiveName = input.next();
				String cityName = input.next();
				double enginePower = input.nextDouble();
				findCity(cityName).addLocomotive(new Locomotive(locomotiveName, enginePower));
			}
			input.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
			}
	}
	
	/**
	 * Reads the input file and calls some other methods in order to complete the missions.
	 * <p>
	 * Assumes that the input file is always valid.
	 * 
	 * @param missionsFileName	The name of the file which stores the missions.
	 */
	private static void completeMissions(String missionsFileName){
		try{
			Scanner input = new Scanner(new File(missionsFileName));
			while( input.hasNextLine() ){
				String line = input.nextLine();
				if( !line.equals("") ){
					String[] currentMission = line.split("-");		
					assembleTrain(findCity(currentMission[0]), Integer.parseInt(currentMission[3]));
					goToMidwayCity(findCity(currentMission[1]), Integer.parseInt(currentMission[4]), currentMission[5]);
					disbandTrain(findCity(currentMission[2]));
				}
			}
			input.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
			}
	}
	
	/**
	 * Picks the most powerful locomotive from the garage and then appends some waggons to it.
	 * 
	 * @param city	The city where a train is assembled.
	 * @param numOfWagsToBeAppended	The number of waggons that are appended to the locomotive.
	 */
	private static void assembleTrain(City city, int numOfWagsToBeAppended){
		train.add(city.getLocomotiveGarage().poll());
		for( int i = 0 ; i < numOfWagsToBeAppended ; i++ ){
			train.add(city.getWaggonGarage().pop());
		}
	}
	
	/**
	 * Moves the train to a new city and takes some waggons from its garage and leaves some waggons at the garage. 
	 * 
	 * @param city	The city where the train goes to after the starting city.
	 * @param numOfWagsToBeAppended	The number of waggons that are taken from the garage of this city.
	 * @param indsOfWagsToBeLeft	The indices of the waggons which are removed from the train and added to the garage of this city. (The indices of the waggons start from 0 after the locomotive.)
	 */
	private static void goToMidwayCity(City city, int numOfWagsToBeAppended, String indsOfWagsToBeLeft){
		// Picks some waggons from the waggon garage of this city
		for( int i = 0 ; i < numOfWagsToBeAppended ; i++ ){
			train.add(city.getWaggonGarage().pop());
		}
		
		String[] indicesOfWags = indsOfWagsToBeLeft.split(",");
		
		// Adds the waggons into the waggon garage of this city without removing them from Doubly LinkedList (train)
		for( int i = 0; i < indicesOfWags.length ; i++ ){
			city.addWaggon(train.get(1 + Integer.parseInt(indicesOfWags[i])));
		}
		
		// Sorts the indices of the waggons to be left in this city from lower to greater in order to facilitate the removing process
		Arrays.sort(indicesOfWags);
		
		// Removes the waggons to be left in this city from the Doubly LinkedList (train)
		for( int i = 0 ; i < indicesOfWags.length ; i++ ){
			train.remove(1 - i + Integer.parseInt(indicesOfWags[i]));
		}
	}
	
	/**
	 * Disbands the train; adds the locomotive and waggons to the corresponding garages in this city.
	 * 
	 * @param city	The final destination of the train.
	 */
	private static void disbandTrain(City city){
		city.addLocomotive((Locomotive)train.remove(0));
		while(train.size() != 0){
			city.addWaggon(train.remove(0));
		}
	}
	
	/**
	 * Finds the City object in <code>cities</code> ArrayList with the given name.
	 *  
	 * @param cityName	The name of the city.
	 * @return	The City object which has this name; null if there is no city with this name.
	 */
	private static City findCity(String cityName){
		for(City c : cities){
			if( c.getName().equals(cityName) ){
				return c;
			}
		}
		return null;
	}
	
	/**
	 * Creates an output file which contains the information about final conditions of the cities and 
	 * their garages.
	 * 
	 * @param outputFileName	The name of the file which stores the information about the final conditions of the cities and their garages.
	 */
	private static void writeOutput(String outputFileName){
		try{
			PrintStream ps = new PrintStream(outputFileName);
			for(City c : cities){
				ps.println(c.getName());
				ps.print("Waggon Garage:");
				ps.println(c.getWaggonGarage().getElements());
				ps.print("Locomotive Garage:");
				ps.println(c.getLocomotiveGarage().getElements());
				ps.println("---------------");
			}
			ps.close();
		}catch(FileNotFoundException e){}
	}
	
	/**
	 * A Doubly LinkedList which is used as a train.
	 */
	private static MyDLL<Waggon> train = new MyDLL<Waggon>();
	
	/**
	 * An ArrayList which stores the cities.
	 */
	private static ArrayList<City> cities = new ArrayList<City>();
	
}
