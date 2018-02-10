package visualization;

import acm.graphics.GCompound;
import acm.graphics.GLabel;
import acm.graphics.GOval;
import acm.graphics.GRect;

/**
 * Creates a vehicle (Car or Bus) object.
 * 
 * @author The assistant(s) of CMPE160
 *
 */
public abstract class Vehicle extends GCompound {

	/**
	 * The body part of the vehicle.
	 */
	protected GRect body;
	
	/**
	 * The windows of the vehicle.
	 */
	protected GRect[] windows;
	
	/**
	 * The wheels of the vehicle.
	 */
	protected GOval[] wheels;
	
	/**
	 * The text which will be written on the vehicle.
	 */
	protected GLabel label;
	
	/**
	 * The width and the height of the body part, the size of a side of the window and the diameter of the wheel.
	 */
	protected int width, height, windowLength, wheelCircle;
	
	/**
	 * The direction in which the vehicle moves and the lane on which the vehicle moves. 
	 */
	protected int direction, lane;

	public abstract void addBody(int objX, int objY);
	
	public abstract void addWindows(int objX, int objY);

	public abstract void addWheels(int objX, int objY);

	public abstract void addLabel(int objX, int objY);
	
	
	/**
	 * Returns the current direction of the vehicle.
	 * 
	 * @return The direction of the vehicle, -1 for left 1 for right.
	 */
	public int getDirection(){
		return direction;
	}
	
	
	/**
	 * Returns the current lane on which the vehicle is moving.
	 * 
	 * @return A "lane number" (1, 2, 3 or 4)
	 */
	public int getLane() {
		return lane;
	}
	
}
