package visualization;

import acm.graphics.GImage;

/**
 * Represents a player with a name and a score.
 * 
 * @author Cemal Burak AYGÜN
 *
 */
public class Player extends GImage {
	
	/**
	 * The name of the player.
	 */
	private String name;
	
	/**
	 * The current total score of the player.
	 */
	private int totalScore;
	
	
	/**
	 * Creates a GImage with the given image path and specifies the name and the total score of the player.
	 * 
	 * @param name			The name of the player.
	 * @param totalScore	The current total score of the player.
	 */
	public Player(String name, int totalScore){
		super("turtle.png");	// The image is 120x138 in size.
		this.name = name;
		this.totalScore = totalScore;
	}
	
	
	/**
	 * Returns the name of the player.
	 * 
	 * @return The name of the player.
	 */
	public String getName(){
		return name;
	}
	
	
	/**
	 * Returns the total score of the player.
	 * 
	 * @return The total score of the player.
	 */
	public int getTotalScore(){
		return totalScore;
	}
	
	
	/**
	 * Updates the total score of the player by adding the parameter to the current total score.
	 * 
	 * @param roundScore The point which is obtained by completing a round.
	 */
	public void updateScore(int roundScore) {
		totalScore += roundScore;
	}
	
}
