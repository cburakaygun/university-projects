package visualization;

/**
 * Creates a turtle in the game.
 * 
 * @author Cemal Burak AYGÜN
 * @see Player
 *
 */
public class Turtle extends Player{
	
	/**
	 * The information about whether the turtle reached the upside or not.
	 */
	private boolean isReachedUpside;
	
	/**
	 * The number of fire the turtle currently has in the game.
	 */
	private int numberOfFire;
	
	
	/**
	 * Creates a <i>Player</i>, sets the initial value of <i>isReachedUpside</i> to false
	 * and sets the initial value of <i>numberOfFire</i> to 3.
	 * <p>
	 * The name of the player is the parameter and the initial total score of the player is 0.
	 * 
	 * @param name The name of the player.
	 */
	public Turtle(String name){
		super(name, 0);		// At the beginning, the total score of the player is 0.
		isReachedUpside = false;	
		numberOfFire = 3;	// The turtle can use its fire ability at most 3 times during the game. 
	}
	

	/**
	 * Returns the information about whether the turtle reached across the road or not.
	 * 
	 * @return The information about whether the turtle reached across the road or not.
	 */
	public boolean isReachedUpside() {
		return isReachedUpside;
	}
	
	
	/**
	 * Sets the value of the information about whether the turtle reached across the road or not to given parameter.
	 * 
	 * @param isReachedUpside The information about whether the turtle reached the upside or not.
	 */
	public void setIsReachedUpside(boolean isReachedUpside) {
		this.isReachedUpside = isReachedUpside;
	}
	
	
	/**
	 * Returns the number of fire the turtle currently has.
	 * 
	 * @return The number of fire the turtle currently has. 
	 */
	public int getNumberOfFire(){
		return numberOfFire;
	}
	
	
	/**
	 * Decreases the number of fire the turtle currently has by 1 if it is greater than 0.
	 */
	public void decreaseNumberOfFire(){
		if(numberOfFire > 0)
			numberOfFire--;
	}

}
