package visualization;

import java.awt.Color;
import java.awt.Font;

import acm.graphics.GLabel;
import acm.graphics.GOval;
import acm.graphics.GRect;

/**
 * Creates a car object with its direction and lane information.
 * <p>
 * A car is gray and it has 2 white windows and 2 black wheels.
 *  
 * @author	Cemal Burak AYGÜN
 * @see		Vehicle
 *
 */
public class Car extends Vehicle{
	
	/**
	 * Specifies the size of the components of the car and calls some other methods.
	 * <p>
	 * These are: createDirection, createLane, addBody, addWindows, addWheels and addLabel.
	 */
	public Car(){
		
		width = 100;
		height = 70;
		windowLength = 35;
		wheelCircle = 24;
		direction = createDirection();
		lane = createLane();
		addBody(0, 0);
		addWindows(0, 0);
		addWheels(0, 0);
		addLabel(0, 0);
		
	}
	
	
	/**
	 * Creates the body part of the car and adds it to the GCompound.
	 * 
	 * @param objX x-coordinate of the body component on the canvas.
	 * @param objY y-coordinate of the body component on the canvas.
	 */
	@Override
	public void addBody(int objX, int objY) {
		
		body = new GRect(width, height);
		body.setFilled(true);
		body.setFillColor(Color.GRAY);
		add(body, objX, objY);
		
	}
	

	/**
	 * Creates the window parts of the car and adds them to the GCompound.
	 * 
	 * @param objX x-coordinate of the <u>body</u> component on the canvas.
	 * @param objY y-coordinate of the <u>body</u> component on the canvas.
	 */
	@Override
	public void addWindows(int objX, int objY) {
		
		windows = new GRect[2];
		for(int i = 0 ; i < 2 ; i++){
			windows[i] = new GRect(windowLength, windowLength-10);
			windows[i].setFilled(true); 
			windows[i].setFillColor(Color.WHITE);
		}
		add(windows[0], objX+10, objY+10);
		add(windows[1], objX+55, objY+10);
		
	}
	

	/**
	 * Creates the wheel parts of the car and adds them to the GCompound.
	 * 
	 * @param objX x-coordinate of the <u>body</u> component on the canvas.
	 * @param objY y-coordinate of the <u>body</u> component on the canvas.
	 */
	@Override
	public void addWheels(int objX, int objY) {
		
		wheels = new GOval[2];
		for (int i=0; i<2; i++) {
			wheels[i] = new GOval(wheelCircle,wheelCircle);
			wheels[i].setFillColor(Color.BLACK);
			wheels[i].setFilled(true); 
		}
		add(wheels[0], objX+10, objY+58);
		add(wheels[1], objX+65, objY+58);
		
	}
	

	/**
	 * Creates a text to be written on the car and adds it to the GCompound.
	 * 
	 * @param objX x-coordinate of the <u>body</u> component on the canvas.
	 * @param objY y-coordinate of the <u>body</u> component on the canvas.
	 */
	@Override
	public void addLabel(int objX, int objY) {
		
		label = new GLabel("CAR");
		label.setFont(new Font("Arial", Font.BOLD, 12));
		label.setColor(Color.WHITE);
		add(label,objX+38,objY+55);
		
	}
	
	
	/**
	 * Specifies the direction (left or right) of the car randomly.
	 * <p>
	 * Each direction has equal chance (%50) to be chosen.
	 * 
	 * @return -1 for left, 1 for right
	 */
	private int createDirection(){
		
		double randomDirection = Math.random();
		if(randomDirection < 0.5) return -1;	// To the left.
		else return 1;							// To the right.
		
	}
	
	
	/**
	 * Reverses the current direction of the car.
	 */
	public void reverseDirection(){
		direction = -direction;
	}
	
	
	/**
	 * Specifies the lane on which the car will appear randomly.
	 * <p>
	 * Each lane has equal chance (%25) to be chosen.
	 *  
	 * @return A "lane number" (1, 2, 3 or 4)
	 */
	private int createLane(){
		
		double randomLane = Math.random();
		if(randomLane < 0.25) return 1;			// The first road from the top of the background image.
		else if(randomLane < 0.5) return 2;		// The second road from the top of the background image.
		else if(randomLane < 0.75) return 3;	// The third road from the top of the background image.
		else return 4;							// The last road from the top of the background image.
		
	}

}
