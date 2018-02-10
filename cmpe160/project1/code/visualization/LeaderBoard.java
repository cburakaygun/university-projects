package visualization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;

/**
 * Creates a leader board with top 10 highest scores in a txt file and shows it in a Message Dialog Box.
 * 
 * @author Cemal Burak AYGÜN
 */
public class LeaderBoard {
	
	/**
	 * The file in which the leader board will be kept.
	 */
	private File file;
	
	/**
	 * The PrintStream which writes the necessary information into <i>file</i>.
	 */
	private PrintStream prntStrm;
	
	/**
	 * The Scanner which reads the information in <i>file</i>.
	 */
	private Scanner scanner;
	
	/**
	 * The ArrayList into which the information in <i>file</i> is written.
	 */
	private ArrayList<Player> players;
	
	
	/**
	 * Specifies <i>file</i> and <i>players</i> fields and calls some other methods.
	 * <p>
	 * These are: updateLeaderBoard and showLeaderBoard
	 * 
	 * @param currentPlayer	Current player whose name and total score will be added to the leader board if appropriate. 
	 * @throws FileNotFoundException
	 */
	public LeaderBoard(Player currentPlayer) throws FileNotFoundException{
		file = new File("LeaderBoard.txt");		// Creates a File with name "LeaderBoard.txt".
		players = new ArrayList<Player>();		// Sets an ArrayList of Player.
		updateLeaderBoard(currentPlayer);
		showLeaderBoard();
	}
	
	
	/**
	 * Creates a txt file named LeaderBoard on the disk if there isn't such a file already
	 * and updates the existing one if there is a high score.
	 * <p>
	 * If there is not a txt file named LeaderBoard on the disk, this method creates the file 
	 * and writes the name and the score of current player together in a row into the file.
	 * <p>
	 * If there is a txt file named LeaderBoard on the disk, this method first copies all the information in the file
	 * to <i>players</i> and then adds current player to the appropriate index in <i>players</i>
	 * and finally overwrites <i>file</i> with the information from <i>players</i>.
	 * <p>
	 * In LeaderBoard.txt, every row contains a player name and the corresponding score.
	 * 
	 * @param currentPlayer	Current player whose name and score will be added to the leader board if appropriate.
	 * @throws FileNotFoundException
	 */
	private void updateLeaderBoard(Player currentPlayer) throws FileNotFoundException{
		
		if(!file.isFile()){		// If there is NOT a txt file named LeaderBoard on the disk,
			
			prntStrm = new PrintStream(file);		// creates a txt file with name LeaderBoard on the disk,
			prntStrm.print("1. " + currentPlayer.getName() + "\t" + currentPlayer.getTotalScore());	// writes current player's name and score into the file and
			players.add(currentPlayer);	// adds current player to "players".
		
		}else{		// If there EXISTS a txt file named LeaderBoard, 
			
			readFile();		// reads all the information in that file.
			
			int indexOfNewPlayer = indexOfNewPlayer(currentPlayer);	// The information about whether current player has a high score or not.
			int numberOfPlayers = players.size();		// The total number of players in "players". (Also, the number of players in LeaderBoard.txt.) 
			
			if(indexOfNewPlayer == -1){		// If current player has NOT a high score...
				
				if(numberOfPlayers < 10){	// ... and if there are less than 10 players in "players",
					
					players.add(numberOfPlayers, currentPlayer);	// adds current player to the end of the "players",
					prntStrm = new PrintStream(file);	// resets the LeaderBoard.txt and
					for(int i = 0 ; i < numberOfPlayers+1 ; i++){	// writes the new leader board into that file.
						prntStrm.println((i+1) + ". " + players.get(i).getName() + "\t" + players.get(i).getTotalScore());
					}
					
				}else{}		// ... and if there are more than or equal to 10 players in "players", does nothing.
			
			}else{		// If current player HAS a high score,
				
				players.add(indexOfNewPlayer, currentPlayer);		// adds current player to the appropriate index in "players",
				prntStrm = new PrintStream(file);		// resets the LeaderBoard.txt and
				for(int i = 0 ; i < Math.min(numberOfPlayers+1, 10) ; i++){		// writes the new leader board into that file.
					prntStrm.println((i+1) + ". " + players.get(i).getName() + "\t" + players.get(i).getTotalScore());
				}
				
			}
			
		}		
	}
	
	
	/**
	 * Reads all the information in LeaderBoard.txt.
	 * 
	 * @throws FileNotFoundException
	 */
	private void readFile() throws FileNotFoundException{
		
		players.clear();		// Removes all the elements if there is any from the "players".
		scanner = new Scanner(file);	// The Scanner which scans the LeaderBoard.txt.
		
		while(scanner.hasNext()){	// At each iteration, creates a Player with the information in one row of LeaderBoard.txt and adds it to "players".
	
			scanner.next();		// Throws away the information of row number (such as "3.") in the file.
			
			String playerName = "";		// The name of the player in LeaderBoard.txt.
			
			/*
			 * This loop takes all the words at the line until the scanner meets an integer (which is the score since I guarantee that the player cannot use a numerical character in his/her PlayerName)
			 * and add these words to "playerName".
			 */
			while(!scanner.hasNextInt()){
				playerName += scanner.next();
			}
			
			Player tempPlayer = new Player(playerName, scanner.nextInt());	// Creates a temporary Player with the name and the related score information in the file. 
			players.add(tempPlayer);		// Adds this temporary Player to the "players".
		
		}
		
	}
	
	
	/**
	 * Checks whether current player has a high score or not. 
	 * 
	 * @param currentPlayer		Current player whose name and score will be added to the leader board if appropriate.
	 * @return					-1 if current player has NOT a high score, index number if current player HAS a high score.
	 */
	private int indexOfNewPlayer(Player currentPlayer){
		
		for(int i = 0 ; i < players.size() ; i++){	// At each iteration, compares current player's score with another player's score from "players".
			if(currentPlayer.getTotalScore() > players.get(i).getTotalScore())
				return i;
		}
		return -1;
		
	}
	
	
	/**
	 * Shows the leader board in a Message Dialog Box.
	 */
	private void showLeaderBoard(){
		
		String leaderBoard = "# .   PLAYER NAME   ( SCORE )\n\n";
		for(int i = 0 ; i < Math.min(players.size(), 10) ; i++){
			leaderBoard += (i+1) + " .   " + players.get(i).getName() + 
					"   ( " + players.get(i).getTotalScore() + " )\n";
		}
		JOptionPane.showMessageDialog(null, leaderBoard, "LEADER BOARD", JOptionPane.INFORMATION_MESSAGE);
	}
	
}
