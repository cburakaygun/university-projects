package visualization;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import acm.graphics.GCanvas;
import acm.graphics.GImage;
import acm.graphics.GObject;

import runnable.Main;

/**
 * Creates a KeyListener so that the player can control the turtle.
 * <p>
 * The turtle can move in four directions with arrow keys and can fire in four directions with WASD keys.
 * 
 * @author Cemal Burak AYGÜN
 *
 */
public class Keyboard implements KeyListener{
	
	/**
	 * The information about whether the turtle can fire or not.
	 * <p>
	 * This field is used to prevent the turtle from firing more than 1 time when one of WASD keys are pressed.
	 */
	private boolean isFireAllowed;
	
	/**
	 * The image of explosion which will be replaced with the vehicle that has exploded.
	 */
	private GImage explosion;
	
	/**
	 * The turtle of the game.
	 */
	private Turtle turtle;
	
	/**
	 * The canvas of the game frame.
	 */
	private GCanvas canvas;
	
	/**
	 * The upper limit that the turtle can go on the y-axis.
	 */
	private double upLimit;
	
	/**
	 * The lower limit that the turtle can go on the y-axis. 
	 */
	private double downLimit;
	
	/**
	 * The lower limit that the turtle can go on the x-axis. 
	 */
	private double leftLimit;
	
	/**
	 * The upper limit that the turtle can go on the x-axis.
	 */
	private double rightLimit;
	
	
	/**
	 * Specifies <i>turtle</i> and <i>canvas</i> fields and sets the value of <i>isFireAllowed</i> to true.
	 * 
	 * @param turtle	The turtle of the game.
	 * @param canvas	The canvas of the game frame.
	 */
	public Keyboard(Turtle turtle, GCanvas canvas){
		this.turtle = turtle;
		this.canvas = canvas;
		isFireAllowed = true;
		upLimit = 0;
		downLimit = canvas.getHeight() - turtle.getHeight();
		leftLimit = 0;
		rightLimit = canvas.getWidth() - turtle.getWidth();
	}
	
	
	/**
	 * Catches the key that is typed on the keyboard.
	 */
	@Override
	public void keyTyped(KeyEvent e){}
	

	/**
	 * Catches the key that is pressed on the keyboard.
	 * <p>
	 * When one of the arrow keys is pressed, this method moves the turtle in the corresponding direction.
	 * <p>
	 * When one of WASD keys is pressed, this method makes the turtle fire its gun in the corresponding direction.
	 * In order for the turtle to hit a vehicle, an imaginary straight line that starts from the center of the turtle
	 * and ends at a side of the frame must intersects with a vehicle. 
	 */
	@Override
	public void keyPressed(KeyEvent e){
		
		int keyCode = e.getKeyCode();	// The value of the key that is pressed on the keyboard.

		if(keyCode == KeyEvent.VK_UP){		// If Up Arrow Key is pressed on the keyboard,
			
			if(turtle.getY() > upLimit){
				turtle.move(0, -5);			// moves the turtle 5 pixels upward.			
			}
			
		}else if (keyCode == KeyEvent.VK_DOWN) {	// If Down Arrow Key is pressed on the keyboard,
			
			if(turtle.getY() < downLimit){
				turtle.move(0, 5);					// moves the turtle 5 pixels downward.
			}
			
		}else if (keyCode == KeyEvent.VK_LEFT) {	// If Left Arrow Key is pressed on the keyboard,
			
			if(turtle.getX() > leftLimit){
				turtle.move(-5, 0);					// moves the turtle 5 pixels to its left.
			}
			
		}else if (keyCode == KeyEvent.VK_RIGHT) {	// If Right Arrow Key is pressed on the keyboard,
			
			if(turtle.getX() < rightLimit){
				turtle.move(5, 0);					// moves the turtle 5 pixels to its right.
			}
			
		}
		
		/*
		 * If the number of fire the turtle currently has is not equal to zero AND fire is allowed AND one of WASD keys is pressed..
		 */
		else if( (turtle.getNumberOfFire() != 0 && isFireAllowed == true) && (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_A ||
				keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_D)){
			
			isFireAllowed = false;	// Sets to false as soon as W is pressed so that the turtle cannot fire more than 1 time at once.
			turtle.decreaseNumberOfFire();	// Decrease the number of fire the turtle currently has by 1.
			
			/*
			 * If W key is pressed, an 'invisible' fire is created at the top of the turtle
			 * and it moves upward until it hits a vehicle or it leaves the canvas without hitting a vehicle.
			 * If a vehicle is hit by the fire, it is exploded by "explode" method.
			 */
			if(keyCode == KeyEvent.VK_W){						
				
				double fireX = turtle.getX() + turtle.getWidth()/2;
				double fireY = turtle.getY();
			
				while(!(canvas.getElementAt(fireX, fireY) instanceof Vehicle) && fireY > 0){
					fireY -= 80;
				}
				
				GObject object = canvas.getElementAt(fireX, fireY);
				
				if(object instanceof Vehicle){				
					explode((Vehicle)object);
				}
		
			}
			
			/*
			 * If A key is pressed, an 'invisible' fire is created at the left side of the turtle
			 * and it moves towards the left until it hits a vehicle or it leaves the canvas without hitting a vehicle.
			 * If a vehicle is hit by the fire, it is exploded by "explode" method.
			 */
			else if(keyCode == KeyEvent.VK_A){	

				double fireX = turtle.getX();
				double fireY = turtle.getY()+turtle.getHeight()/2;
			
				while(!(canvas.getElementAt(fireX, fireY) instanceof Vehicle) && fireX > 0){
					fireX -= 90;
				}
				
				GObject object = canvas.getElementAt(fireX, fireY);
				
				if(object instanceof Vehicle){			
					explode((Vehicle)object);
				}
		
			}
		
			/*
			 * If S key is pressed, an 'invisible' fire is created at the bottom of the turtle
			 * and it moves downward until it hits a vehicle or it leaves the canvas without hitting a vehicle.
			 * If a vehicle is hit by the fire, it is exploded by "explode" method.
			 */
			else if(keyCode == KeyEvent.VK_S){				
			
				double fireX = turtle.getX()+turtle.getWidth()/2;
				double fireY = turtle.getY()+turtle.getHeight();
			
				while(!(canvas.getElementAt(fireX, fireY) instanceof Vehicle) && fireY < 750){
					fireY += 80;
				}
			
				GObject object = canvas.getElementAt(fireX, fireY);
				
				if(object instanceof Vehicle){			
					explode((Vehicle)object);
				}
				
			}
		
			/*
			 * If D key is pressed, an 'invisible' fire is created at the right side of the turtle
			 * and it moves towards the right until it hits a vehicle or it leaves the canvas without hitting a vehicle.
			 * If a vehicle is hit by the fire, it is exploded by "explode" method.
			 */
			else{	// keyCode == KeyEvent.VK_D
				
				double fireX = turtle.getX()+turtle.getWidth();
				double fireY = turtle.getY()+turtle.getHeight()/2;
			
				while(!(canvas.getElementAt(fireX, fireY) instanceof Vehicle) && fireX < 1051){
					fireX += 90;
				}
			
				GObject object = canvas.getElementAt(fireX, fireY);
				
				if(object instanceof Vehicle){		
					explode((Vehicle)object);
				}
			
			}	
		
		}
		
	}
	

	/**
	 * Catches the key that is released on the keyboard.
	 * <p>
	 * When one of WASD keys is released, this method removes the explosion image which has appeared when one of those keys is pressed.
	 */
	@Override
	public void keyReleased(KeyEvent e){
		
		int keyCode = e.getKeyCode();	// The value of the key that is released on the keyboard.
		
		if( (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_D) 
				&& isFireAllowed == false ){
			
			isFireAllowed = true;	// Sets to true as soon as one of WASD key is released so that the turtle can fire again.
			
			long time = System.currentTimeMillis();		// The time when the key is released.
			while( System.currentTimeMillis()  - time < 30 ){}	// Waits 30 milliseconds and

			canvas.remove(explosion);	// removes the explosion image from the canvas.

		}
		
	}
	
	
	/**
	 * Creates an explosion (image), removes the vehicle from both the canvas and <i>vehicles</i> ArrayList in Main.java and
	 * then places the explosion image to the location of the removed vehicle.
	 * 
	 * @param vehicle The vehicle which is hit by a fire and will explode.
	 */
	private void explode(Vehicle vehicle){
		
		explosion = new GImage("explode.png");	// The image is 160x130 in size.
		
		// The location of the explosion image is such that the center of it coincides with the center of the exploded vehicle.
		explosion.setLocation(vehicle.getX()+(vehicle.getWidth()-explosion.getWidth())/2, vehicle.getY()+(vehicle.getHeight()-explosion.getHeight())/2);
		
		Main.vehicles.remove(vehicle);	// Removes the exploded vehicle from "vehicles" ArrayList.
		canvas.remove(vehicle);			// Removes the exploded vehicle from the canvas of the frame.
		canvas.add(explosion);			// Adds the explosion image to the location of the exploded vehicle.
		
	}

}