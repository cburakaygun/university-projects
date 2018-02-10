package visualization;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JFrame;

import acm.graphics.GCanvas;
import acm.graphics.GCompound;
import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GObject;
import acm.graphics.GRect;

/**
 * Creates a frame and a canvas for the game and adds the objects (turtle, vehicles, etc.) to the canvas.
 * 
 * @author Cemal Burak AYGÜN
 *
 */
public class Board implements BoardIntf {
	
	/**
	 * The general frame of the game window. 
	 */
	private JFrame frame;
	
	/**
	 * The canvas of the game frame.
	 */
	private GCanvas canvas;
	
	/**
	 * The background image of the game.
	 */
	private GImage background;
	
	/**
	 * The safe zone area of the game.
	 */
	private GCompound safeZone;
	
	/**
	 * The text which stores the name of the player.
	 */
	private GLabel nameLabel;
	
	/**
	 * The text which stores the current round number of the game.
	 */
	private GLabel roundLabel; 
	
	/**
	 * The text which stores the current total score of the player.
	 */
	private GLabel scoreLabel;
	
	/**
	 * The text which stores the information about the number of fire the turtle currently has.
	 */
	private GLabel numberOfFireLabel;
	
	/**
	 * The text which stores the speed of the turtle in terms of pixels.
	 */
	private GLabel speedOfTurtleLabel;
	
	/**
	 * The text which stores the speed of the vehicles in terms of pixels.
	 */
	private GLabel speedOfVehiclesLabel;
	
	/**
	 * The current round number of the game.
	 */
	private int round;
	
	/**
	 * The turtle of the game.
	 */
	private Turtle turtle;
	
	
	/**
	 * Sets initial value of the round to 1 and calls some other methods.
	 * <p>
	 * These are: setCanvas, setBackground, addGameInfoLabels, addTurtle and addKeyBoardListener
	 * 
	 * @param turtle The turtle of the game.
	 */
	public Board(Turtle turtle) {
		
		round = 1;	// Sets the round to 1 at the beginning of the game.
		this.turtle = turtle;
		setCanvas("Sprinter Turtle", 1051, 770);	// Sets the canvas. (The resolution of my computer screen is 1280x800.)
		setBackground();	// Sets the background image of the game.
		addGameInfoLabels();	// Adds the information labels to the board.
		addTurtle();	// Adds the turtle to the board.
		addKeyBoardListener();	// Adds a KeyListener to the frame and the canvas.
		
	}
	
	
	/**
	 * Returns the current round number of the game.
	 * 
	 * @return The current round number of the game.
	 */
	public int getRound(){
		return round;
	}
	
	
	/**
	 * Increases the current round number of the game by 1.
	 */
	public void increaseRound(){
		round++;
	}
	
	
	/**
	 * Specifies the general frame of the game and adds the canvas to the frame.
	 * 
	 * @param boardName	The title of the game window.
	 * @param width		The width of the game window.
	 * @param height	The height of the game window.
	 */
	@Override
	public void setCanvas(String boardName, int width, int height) {
		
		frame = new JFrame(boardName);	// Creates a frame with the given name.
		frame.setSize(width, height);	// Sets the size of the frame.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);	// Sets the frame in the middle on the screen.
		frame.setVisible(true);			// Makes the frame visible.
		canvas = new GCanvas();		// Creates a canvas.		
		canvas.setSize(frame.getContentPane().getSize());
		frame.getContentPane().add(canvas);		// Adds the canvas to the frame.
		
	}
	
	
	/**
	 * Returns the canvas field of the Board.
	 * 
	 * @return The canvas field of the Board.
	 */
	public GCanvas getCanvas() {
		return canvas;
	}
	

	/**
	 * Sets the background image of the board.
	 */
	@Override
	public void setBackground() {
		
		background = new GImage("asfalt.jpg");	// The image is 1300x866 in size.
		background.setSize(1051, 700);	// Because of the resolution of my computer screen (1200x800), I have to resize the image.
		addObject(background);		// Adds the background image to the canvas.
		background.sendBackward();	// Sends the background image to backward on the canvas so that every other object will appear on the top of this image.
		
	}
	

	/**
	 * Specifies the information labels, their fonts and locations and then adds them to the canvas.
	 */
	@Override
	public void addGameInfoLabels() {
		
		safeZone = new GCompound();
		
		// A white ground for the labels.
		GRect ground = new GRect(background.getWidth(), 70);
		ground.setFilled(true);
		ground.setColor(Color.WHITE);
		safeZone.add(ground);
		
		// Since the contents of the labels below remain the same during the game, they are written here.
		nameLabel = new GLabel("NAME: " + turtle.getName());
		nameLabel.setFont(new Font("Verdana", Font.BOLD, 13));
		safeZone.add(nameLabel, 10, 20);		
		
		speedOfTurtleLabel = new GLabel("SPEED OF TURTLE: 5.0  (Arrow Keys to move)");
		speedOfTurtleLabel.setFont(new Font("Verdana", Font.BOLD, 13));		
		safeZone.add(speedOfTurtleLabel,650,40);			
		
		// Since the contents of the labels below may be changed during the game, they are written in updateGameInfoLabels().
		roundLabel = new GLabel("ROUND");
		roundLabel.setFont(new Font("Verdana", Font.BOLD, 13));
		safeZone.add(roundLabel, 10 , 40);
		
		scoreLabel = new GLabel("");
		scoreLabel.setFont(new Font("Verdana", Font.BOLD, 13));		
		safeZone.add(scoreLabel, 10 , 60);
		
		numberOfFireLabel = new GLabel("");
		numberOfFireLabel.setFont(new Font("Verdana", Font.BOLD, 13));		
		safeZone.add(numberOfFireLabel, 650, 20);				
		
		speedOfVehiclesLabel = new GLabel("");
		speedOfVehiclesLabel.setFont(new Font("Verdana", Font.BOLD, 13));		
		safeZone.add(speedOfVehiclesLabel, 650, 60);
		
		safeZone.setLocation(0, 660);
		addObject(safeZone);
		
	}

	
	/**
	 * Updates the information labels of the game.
	 * <p>
	 * Only 4 labels are updated during the game: roundLabel, scoreLabel, numberOfFireLabel, speedOfVehiclesLabel.
	 */
	public void updateGameInfoLabels(){
		
		roundLabel.setLabel("ROUND: " + round);
		scoreLabel.setLabel("SCORE: " + turtle.getTotalScore());	
		numberOfFireLabel.setLabel("NUMBER OF FIRE: " + turtle.getNumberOfFire() + "   (WASD to fire)");
		speedOfVehiclesLabel.setLabel("SPEED OF VEHICLES: " + (0.6*round+0.4));
		
	}

	
	/**
	 * Scales the turtle (image) and adds it to a specified location on the canvas. 
	 */
	private void addTurtle(){
		turtle.scale(0.4);				// Scales the turtle (image) so that it appears as 48x55 in size.
		turtle.setLocation((canvas.getWidth()-turtle.getWidth())/2, safeZone.getY()+5);	// Sets the location of the turtle to the specified point.
		addObject(turtle);				// Adds the turtle to the canvas.
	}
	
	
	/**
	 * Creates a Keyboard object which is a KeyListener and adds it to both the frame and the canvas.
	 */
	public void addKeyBoardListener(){
		Keyboard keyboard = new Keyboard(turtle, canvas);	// Creates a KeyListener.
		frame.addKeyListener(keyboard);		// Adds it to the frame.
		canvas.addKeyListener(keyboard);	// Adds it to the canvas.
	}
	

	/**
	 * Adds the given GObject to the canvas.
	 * 
	 * @param g The GObject to be added to the canvas.
	 */
	@Override
	public void addObject(GObject g) {
		canvas.add(g);
	}

	
	/**
	 * Makes the main loop of the game wait for the <i>parameter</i>  milliseconds between each iteration.
	 * 
	 *  @param millisecs The time interval that the main loop is paused between each iteration.
	 */
	@Override
	public void waitFor(long millisecs) {
		
		try {
			Thread.sleep(millisecs);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
