package runnable;

import java.awt.Color;
import java.awt.Font;

import java.io.FileNotFoundException;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import acm.graphics.GLabel;
import acm.graphics.GObject;

import visualization.Board;
import visualization.Vehicle;
import visualization.Car;
import visualization.Bus;
import visualization.Turtle;
import visualization.LeaderBoard;

/**
 * Works as the game engine: Creates new vehicles, moves them, removes them if they are out of the canvas, 
 * checks for collisions, updates score, etc. 
 * 
 * @author Cemal Burak AYGÜN
 *
 */
public class Main {
	
	/**
	 *  The list of the vehicles that are active in the game.
	 */
	public static ArrayList<Vehicle> vehicles;
	
	/**
	 * The turtle of the game.
	 */
	private static Turtle turtle;
	
	/**
	 * The board of the game.
	 */
	private static Board board;
	
	
	/**
	 * Initializes <i>vehicles</i>, <i>turtle</i> and <i>board</i>, then using a while loop keeps the game running.
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException{
		
		vehicles = new ArrayList<Vehicle>();		
		turtle = new Turtle(getUserName());
		board = new Board(turtle);	

		while(true){	// Main loop of the game.
			
			if(checkForCollision()){
				gameOver();
				new LeaderBoard(turtle);
				break;		// Ends the loop if there is a collision between the turtle and a vehicle, i.e. When the game is over.
			}			
			
			Vehicle newVehicle = createVehicle();
			
			if(newVehicle != null){			// If newly created vehicle is not null,
				vehicles.add(newVehicle);	// adds it to "vehicles".
			}
			
			moveVehicles();			
			removeVehicles();			
			updateScore();					
			board.waitFor(30);		// Waits for 30 milliseconds after each iteration.
			
		}
		
	}
	
	
	/**
	 * Asks the player to write a player name in a Input Dialog Box to start the game.
	 * <p>
	 * Player name can neither be empty nor contain a numeric character. 
	 * The input box keeps popping up as long as these 2 requirements are not satisfied.
	 * 
	 * @return The name of the player.
	 */
	private static String getUserName(){
		
		String name = "";	// The initial player name.
		while(name.trim().length() <= 0 || name.matches(".*\\d+.*")){		// The name of the player cannot be empty and cannot contain a number.
			name = JOptionPane.showInputDialog(null, "Enter your PlayerName to begin:\n\nIt can neither be empty nor contain a number.\n\n", "PLAYER NAME?", JOptionPane.QUESTION_MESSAGE);
		}
		return name;
		
	}
	
	
	/**
	 * Checks for a collision between the turtle and a vehicle and also between any two vehicles.
	 * <p>
	 * Firstly, checks for a collision between the turtle and a vehicle.
	 * Looks for whether a vehicle intersects with one of the four corners of the turtle (image) or not.
	 * <p>
	 * Secondly, if there is not any collisions between the turtle and a vehicle, checks for collisions between vehicles.
	 * For every vehicle on the screen, looks for whether there is another vehicle to the left (of 2 pixels) of it or not.
	 * If there is a collision (if the space between the two vehicles is 2 pixels or less in width), 
	 * reverses both of the vehicles' directions.
	 * 
	 * @return If there is a collision between the turtle and a vehicle, true. Else, false.
	 */
	private static boolean checkForCollision(){
		
		// *** The part below checks for a collision between the turtle and a vehicle.
		
		double trtl_W = turtle.getWidth();		// The width of the turtle.
		double trtl_H = turtle.getHeight(); 	// The height of the turtle.
		double trtl_X = turtle.getX();			// The x-coordinate of the turtle.
		double trtl_Y = turtle.getY();			// The y-coordinate of the turtle.
				
		// Checks whether a Vehicle intersects with one of the four corners of the turtle (image).
		if(board.getCanvas().getElementAt(trtl_X, trtl_Y) instanceof Vehicle ||					// Upper-left corner of the turtle.
				board.getCanvas().getElementAt(trtl_X+trtl_W, trtl_Y) instanceof Vehicle ||		// Upper-right corner of the turtle.
				board.getCanvas().getElementAt(trtl_X, trtl_Y+trtl_H) instanceof Vehicle ||		// Lower-left corner of the turtle.
				board.getCanvas().getElementAt(trtl_X+trtl_W, trtl_Y+trtl_H) instanceof Vehicle	// Lower-right corner of the turtle.
				){
			return true;	// If there is a collision between the turtle and a vehicle.
		}
		
		// *** The part below checks for collisions between any two vehicles.
		
		for(int i = 0 ; i < vehicles.size() ; i++){
			
			Vehicle thisVehicle = vehicles.get(i);
			
			// The GObject which is to the left (of 2 pixels) of thisVehicle.
			GObject other = board.getCanvas().getElementAt(thisVehicle.getX()-2, thisVehicle.getY()+45);
			
			// If the two vehicles are moving in opposite directions, reverses both of their directions.
			if( other instanceof Vehicle && ((Vehicle) other).getDirection() != thisVehicle.getDirection() ){
				
				/*
				 * Since I am not allowed to change Vehicle.java, I had to write the same methods in Car.java and Bus.java.
				 * So, I need to typecast Vehicle to be able to use the methods.
				 */				
				if(thisVehicle instanceof Car){
					((Car)thisVehicle).reverseDirection();
				}else{	// thisVehicle is a bus
					((Bus)thisVehicle).reverseDirection();
				}				
				
				if(other instanceof Car){
					((Car)other).reverseDirection();
				}else{	// other is a bus
					((Bus)other).reverseDirection();
				}
				
			}
			
		}
		
		return false;		// If there is NOT any collisions between the turtle and a vehicle.
		
	}
	
	
	/**
	 * Creates a text which says "!! GAME OVER !!" and adds it to the canvas.
	 */
	private static void gameOver(){
		
		GLabel gameOver = new GLabel("!! GAME OVER !!");
		gameOver.setFont(new Font("Arial", Font.BOLD, 32));
		gameOver.setColor(Color.RED);
		gameOver.setLocation(300, board.getCanvas().getHeight()-30);
		board.addObject(gameOver);
		
	}
	

	/**
	 * Creates a new vehicle of a type (car or bus) and places it in somewhere in the canvas if there is enough space for that vehicle.
	 * <p>
	 * The creation, the type and the location of the vehicle is decided randomly.
	 * <p>
	 * This method creates a car with %2 probability, a bus with %2 probability and nothing (null) with %96 probability.
	 * <p>
	 * The lane on which a vehicle moves is specified by that vehicle's class (Car or Bus).
	 * This method decides at which side (left or right) of the lane this vehicle will appear.
	 * Each side of the lane has equal chance (%50) to be chosen.
	 * <p>
	 * If the area has not enough space for newly created vehicle to fit in, it is discarded.
	 * 
	 * @return If a vehicle is created and the lane is available, the newly created vehicle. Else, null.
	 */
	private static Vehicle createVehicle() {
		
		double randomCreation = Math.random();
		Vehicle newVehicle;		// The vehicle that may be created.
		
		if(randomCreation < 0.02){	// A car is created if randomCreation is in the range [0, 0.02).
			newVehicle = new Car();
		}else if(randomCreation < 0.04){	// A bus is created if randomCreation is in the range [0.02, 0.04).
			newVehicle = new Bus();
		}else{						// No vehicle is created if randomCreation is greater than or equal to 0.04.
			return null;
		}
		
		double veh_W = newVehicle.getWidth();	// The width of newly created vehicle.
		double veh_H = newVehicle.getHeight();	// The height of newly created vehicle.
		int veh_lane = newVehicle.getLane();	// The "lane number" of newly created vehicle.
		int control_Y;							// The y-coordinate to be used to check whether the lane is available or not.
		
		double randomSide = Math.random();
		
		if(veh_lane == 1){			// The first road from the top of the background image.
			
			if(randomSide < 0.5){
				newVehicle.setLocation(0, 162-veh_H);	// The left side of the road.
			}else{
				newVehicle.setLocation(1051 - veh_W, 162-veh_H);	// The right side of the road.
			}		
			
			control_Y = 80;
		
		}else if(veh_lane == 2){	// The second road from the top of the background image.
			
			if(randomSide < 0.5){
				newVehicle.setLocation(0, 324-veh_H);	// The left side of the road.
			}else{
				newVehicle.setLocation(1051 - veh_W, 324-veh_H);	// The right side of the road.
			}
			
			control_Y = 244;
		
		}else if(veh_lane == 3){	// The third road from the top of the background image.
			
			if(randomSide < 0.5){
				newVehicle.setLocation(0, 486-veh_H);	// The left side of the road.
			}else{
				newVehicle.setLocation(1051 - veh_W, 486-veh_H);	// The right side of the road.
			}
			
			control_Y = 404;
		
		}else{						// The forth road from the top of the background image.
			
			if(randomSide < 0.5){
				newVehicle.setLocation(0, 648-veh_H);	// The left side of the road.
			}else{
				newVehicle.setLocation(1051 - veh_W, 648-veh_H);	// The right side of the road.
			}
			
			control_Y = 568;
		
		}
		
		double veh_x = newVehicle.getX();	// The x-coordinate of newly created vehicle.
		
		// Checks whether there is another vehicle in the area to which newly created vehicle is supposed to be added.
		if( board.getCanvas().getElementAt(veh_x, control_Y) instanceof Vehicle ||					// A point from the left part of newly created vehicle.
				board.getCanvas().getElementAt(veh_x+(veh_W/2), control_Y) instanceof Vehicle ||	// A point from the middle part of newly created vehicle.
				board.getCanvas().getElementAt(veh_x+veh_W, control_Y) instanceof Vehicle ){		// A point from the right part of newly created vehicle.
			return null;
		}else{		// If the lane is available,
			board.addObject(newVehicle);		// adds newly created vehicle to the canvas.
			return newVehicle;
		}
		
	}
	

	/**
	 * Moves every vehicle in <i>vehicles</i> in the direction of that vehicle's <i>direction</i> field with a constant speed.
	 * <p>
	 * The speed of every vehicle is the same and is a function of the current round number. (V = 0.6*round + 0.4)
	 */
	private static void moveVehicles() {
		
		for(int i = 0 ; i < vehicles.size() ; i++){
			Vehicle thisVehicle = vehicles.get(i);
			thisVehicle.move(thisVehicle.getDirection()*(0.6*board.getRound()+0.4), 0);	// Moves the vehicle in the x-direction.
		}

	}
	
	
	/**
	 * Removes the vehicles, which are out of the canvas boundary by their half width, from both the canvas and <i>vehicles</i>.
	 */
	private static void removeVehicles(){
		
		for(int i = 0 ; i < vehicles.size() ; i++){
			Vehicle vehicle = vehicles.get(i);
			
			// Checks whether the x-coordinate of the "vehicle" is out of the specified borders.
			if( vehicle.getX() < -vehicle.getWidth()*0.5 || vehicle.getX() > 1051 - vehicle.getWidth()*0.5 ){	// The width of the frame is 1051 pixels.
				board.getCanvas().remove(vehicle);		// Removes "vehicle" from the canvas.
				vehicles.remove(i);						// Removes "vehicle" from "vehicles" ArrayList.
			}			
		}
		
	}
	
	
	/**
	 * Updates the round number and the total score of the player if a round is completed.
	 * <p>
	 * A round is completed when the turtle reaches upside and comes back to "safe zone".
	 * If the round is completed, the player gets the round number as points and the number of round is increased by 1.
	 * 
	 *@see Board
	 */
	private static void updateScore(){

		// Checks whether the turtle has reached the upside or not.
		if(turtle.getY() < 3){
			turtle.setIsReachedUpside(true);
		}
		
		// Checks whether the turtle came back to "safe zone" after reaching the upside.
		if(turtle.isReachedUpside() && turtle.getY() > 660){
			
			turtle.setIsReachedUpside(false);
			turtle.updateScore(board.getRound());	// Gives the player some points (the number of the round).
			board.increaseRound();					// Increases the round number by 1.
			
		}
		
		board.updateGameInfoLabels();				// Updates the information labels on the canvas.
		
	}

}