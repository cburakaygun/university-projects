package visualization;

import java.awt.Color;
import java.awt.Font;

import acm.graphics.GLabel;
import acm.graphics.GOval;
import acm.graphics.GRect;

/**
 * Creates a bus object with its direction and lane information.
 * <p>
 * The bus is green and it has 2 white windows and 4 black wheels.
 *  
 * @author	Cemal Burak AYGÜN
 * @see 	Vehicle
 *
 */
public class Bus extends Vehicle {
	
	/**
	 * Specifies the size of the components of the bus and calls some other methods.
	 * <p>
	 * These are: createDirection, createLane, addBody, addWindows, addWheels and addLabel
	 */
	public Bus(){
		
		width = 180;
		height = 110;
		windowLength = 65;
		wheelCircle = 30;
		direction = createDirection();
		lane = createLane();
		addBody(0, 0);
		addWindows(0, 0);
		addWheels(0, 0);
		addLabel(0, 0);
		
	}
	
	
	/**
	 * Creates the body part of the bus and adds it to the GCompound.
	 * 
	 * @param objX x-coordinate of the body component on the canvas.
	 * @param objY y-coordinate of the body component on the canvas.
	 */
	public void addBody(int objX, int objY){
		
		body = new GRect(width, height);
		body.setFilled(true);
		body.setFillColor(Color.GREEN);
		add(body, objX, objY);
		
	}
	
	
	/**
	 * Creates the window parts of the bus and adds them to the GCompound.
	 * 
	 * @param objX x-coordinate of the <u>body</u> component on the canvas.
	 * @param objY y-coordinate of the <u>body</u> component on the canvas.
	 */
	public void addWindows(int objX, int objY){
		
		windows = new GRect[2];
		for(int i = 0 ; i < 2 ; i++){
			windows[i] = new GRect(windowLength, windowLength-30);
			windows[i].setFilled(true); 
			windows[i].setFillColor(Color.WHITE);
		}
		add(windows[0], objX+15, objY+10);
		add(windows[1], objX+100, objY+10);
		
	}

	
	/**
	 * Creates the wheel parts of the bus and adds them to the GCompound.
	 * 
	 * @param objX x-coordinate of the <u>body</u> component on the canvas.
	 * @param objY y-coordinate of the <u>body</u> component on the canvas.
	 */
	public void addWheels(int objX, int objY){
		
		wheels = new GOval[4];
		for (int i=0; i<4; i++) {
			wheels[i] = new GOval(wheelCircle,wheelCircle);
			wheels[i].setFillColor(Color.BLACK);
			wheels[i].setFilled(true); 
		}
		add(wheels[0], objX+12, objY+95);
		add(wheels[1], objX+54, objY+95);
		add(wheels[2], objX+96, objY+95);
		add(wheels[3], objX+138, objY+95);
		
	}
	

	/**
	 * Creates a text to be written on the bus and adds it to the GCompound.
	 * 
	 * @param objX X-coordinate of the <u>body</u> component on the canvas.
	 * @param objY Y-coordinate of the <u>body</u> component on the canvas.
	 */
	public void addLabel(int objX, int objY){
		
		label = new GLabel("BUS");
		label.setFont(new Font("Arial", Font.BOLD, 24));
		label.setColor(Color.WHITE);
		add(label,objX+64,objY+80);
		
	}
	
	
	/**
	 * Specifies the direction (left or right) of the bus randomly.
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
	 * Reverses the current direction of the bus.
	 */
	public void reverseDirection(){
		direction = -direction;
	}
	
	
	/**
	 * Specifies the lane on which the bus will appear randomly.
	 * <p>
	 * Each lane has equal chance (%25) to be chosen.
	 *  
	 * @return A "lane number" (1, 2, 3 or 4)
	 */
	public int createLane(){
		
		double randomLane = Math.random();
		if(randomLane < 0.25) return 1;			// The first road from the top of the background image.
		else if(randomLane < 0.5) return 2;		// The second road from the top of the background image.
		else if(randomLane < 0.75) return 3;	// The third road from the top of the background image.
		else return 4;							// The last road from the top of the background image.
		
	}

}
