import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.robotics.subsumption.Behavior;

/**
 * Moves the Robot in a Depth First Search (DFS) manner and examines the environment
 * to construct the actual map.
 * 
 * As new cells are discovered, modifies `Cell.cells` and sends data to PC over socket.
 * 
 * Assigns the initial direction of the Robot as NORTH.
 * Assumes that the Robot is in a 7x7 grid and it is in the middle cell initially. (Actual map is a 4x4 grid.)
 * 
 */
public class Mapping implements Behavior {
	
	private Cell currentCell;
	private boolean suppressed;

	@Override
	public boolean takeControl() {
		return (Button.UP.isDown() || TermProject.resetMapping);
	}

	@Override
	public void action() {
		Sound.twoBeeps();
		TermProject.resetMapping = false;
		suppressed = false;
    	TermProject.currentTaskID = TermProject.TASK_ID_MAPPING;

    	try {
			TermProject.dataOutputStream.writeInt(TermProject.TASK_ID_MAPPING);
	    	TermProject.dataOutputStream.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		Cell.resetCells();
   		Robot.ROBOT_DIR = Direction.NORTH;
   		Robot.rotateUltrasonicToRobotDirection();
   		
   		int[] remainingNeighbors = new int[Cell.cells.length]; // Stores the number of neighbors that are not discovered yet for each cell
   		Arrays.fill(remainingNeighbors, 4);
   		
   		int[] comingDirection = new int[Cell.cells.length]; // Stores the direction along which the Robot traveled to reach the cell
   		Arrays.fill(comingDirection, Direction.UNKNOWN);
        
   		int prevIndex = -1; // Index of the previous cell the Robot was present
        Cell prevCell; // Previous cell the Robot was present
        
        Robot.CURRENT_INDEX = 24; // Assumes that the Robot is in the middle cell of a 7x7 grid (actual map is 4x4 grid)
        if (Cell.cells[Robot.CURRENT_INDEX] == null) {
        	Cell.cells[Robot.CURRENT_INDEX] = new Cell();
        }
        currentCell = Cell.cells[Robot.CURRENT_INDEX];

        Cell.setColor(currentCell);
        try {
			sendData();
		} catch (IOException e) {
			e.printStackTrace();
		}

        mainLoop: while (true) {
        	if (suppressed) {
        		break mainLoop;
        	}
        	
            if (remainingNeighbors[Robot.CURRENT_INDEX] == 0) { // All the neighbors are discovered
                if (comingDirection[Robot.CURRENT_INDEX] == Direction.UNKNOWN) { // The robot is on the starting cell
                	break mainLoop; // Mapping is done
                } else {
                	try {
						leaveCell(comingDirection[Robot.CURRENT_INDEX]);
					} catch (IOException e) {
						e.printStackTrace();
					} // Returns the Robot to the previous cell
                }
            }

            else if (currentCell.neighbors[Robot.ULTRASONIC_DIR] != Cell.UNKNOWN) { // This neighbor has already been discovered
            	Robot.rotateUltrasonicLeft();
            }

            else if (Robot.isWallPresent()) {
            	currentCell.neighbors[Robot.ULTRASONIC_DIR] = Cell.WALL;
            	remainingNeighbors[Robot.CURRENT_INDEX]--;
            	Robot.rotateUltrasonicLeft();
            }

            else { // This neighbor has not been discovered yet
            	Robot.rotateToDirection(Robot.ULTRASONIC_DIR);
            	Robot.rotateUltrasonicToRobotDirection();
            	Robot.travel(Robot.TRAVEL_AMOUNT);
                
            	prevIndex = Robot.CURRENT_INDEX;
                switch (Robot.ROBOT_DIR) {
                    case Direction.NORTH:
                    	Robot.CURRENT_INDEX -= 7;
                        break;
                    case Direction.EAST:
                    	Robot.CURRENT_INDEX += 1;
                        break;
                    case Direction.SOUTH:
                    	Robot.CURRENT_INDEX += 7;
                        break;
                    case Direction.WEST:
                    	Robot.CURRENT_INDEX -= 1;
                        break;
                }
                
                if (Cell.cells[Robot.CURRENT_INDEX] == null) {
                	Cell.cells[Robot.CURRENT_INDEX] = new Cell();
                }
                currentCell = Cell.cells[Robot.CURRENT_INDEX];

                if (comingDirection[Robot.CURRENT_INDEX] == Direction.UNKNOWN) { // First time visit to this cell
                    prevCell = Cell.cells[prevIndex];
                    prevCell.neighbors[Robot.ROBOT_DIR] = Robot.CURRENT_INDEX;
                    remainingNeighbors[prevIndex]--;
                    
                    comingDirection[Robot.CURRENT_INDEX] = Robot.ROBOT_DIR;
                    currentCell.neighbors[(Robot.ROBOT_DIR+2)%4] = prevIndex;
                    remainingNeighbors[Robot.CURRENT_INDEX]--;

                    Cell.setColor(currentCell);
                    if (currentCell.color == Colour.BLACK) {
                        try {
							leaveCell(comingDirection[Robot.CURRENT_INDEX]);
						} catch (IOException e) {
							e.printStackTrace();
						}
                    }
                }
            }
            
            try {
				sendData();
			} catch (IOException e) {
				e.printStackTrace();
			}
            
        }
		
		if (!suppressed) {
			Cell.analyzeCells();
			try { // Writes map (cells) to file
		    	FileOutputStream fileOutputStream = new FileOutputStream(new File(TermProject.MAP_FILE_NANE));
		    	ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
				objectOutputStream.writeObject(Cell.cells);
				objectOutputStream.close();
				fileOutputStream.close();
	    	} catch (IOException e) {
				Sound.buzz();
				Robot.writeToLCD("MAP FILE W ERROR");
				return;
	    	}
			Robot.rotateUltrasonicToRobotDirection();
			Robot.beep3Times();
		} else {
			Cell.cells = null;
		}

    	try {
			TermProject.dataOutputStream.writeInt(TermProject.TASK_ID_MAPPING);
	    	TermProject.dataOutputStream.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
	}

	@Override
	public void suppress() {
		suppressed = true;
	}
	
	// Moves the Robot backwards to the previous cell from `currentCell`.
	// currentCell		: The cell on which the Robot currently stands
	// comingDirection	: The direction along which the Robot traveled to reach `currentCell`
	private void leaveCell(int comingDirection) throws IOException {
        sendData();
        
		int directionDistance = Robot.ROBOT_DIR - comingDirection;
    	if (directionDistance == -1 || directionDistance == 3) {
    		Robot.rotateRight();
    	}
    	else if (directionDistance == -3 || directionDistance == 1) {
    		Robot.rotateLeft();
    	}
    	else if (directionDistance == -2 || directionDistance == 2) {
    		Robot.rotateBack();
    	}
    	
    	sendData();
    	
    	Robot.travel(-1 * Robot.TRAVEL_AMOUNT);
    	
    	Robot.CURRENT_INDEX = currentCell.neighbors[(comingDirection+2)%4];
    	Robot.rotateUltrasonicToRobotDirection();
    	
    	currentCell = Cell.cells[Robot.CURRENT_INDEX];
	}
	
	// Sends the following information to the socket in order:
	// - The index of the cell on which the Robot currently stands
	// - The direction of the Robot
	// - Color of that cell
	// - North-Neighbor of that cell
	// - East-Neighbor of that cell
	// - South-Neighbor of that cell
	// - West-Neighbor of that cell
    private void sendData() throws IOException {    	
    	TermProject.dataOutputStream.writeInt(Robot.CURRENT_INDEX);
    	TermProject.dataOutputStream.flush();
    	
    	TermProject.dataOutputStream.writeInt(Robot.ROBOT_DIR);
    	TermProject.dataOutputStream.flush();
    	
    	TermProject.dataOutputStream.writeInt(currentCell.color);
    	TermProject.dataOutputStream.flush();
    	
    	TermProject.dataOutputStream.writeInt(currentCell.neighbors[Direction.NORTH]);
    	TermProject.dataOutputStream.flush();
    	
    	TermProject.dataOutputStream.writeInt(currentCell.neighbors[Direction.EAST]);
    	TermProject.dataOutputStream.flush();
    	
    	TermProject.dataOutputStream.writeInt(currentCell.neighbors[Direction.SOUTH]);
    	TermProject.dataOutputStream.flush();
    	
    	TermProject.dataOutputStream.writeInt(currentCell.neighbors[Direction.WEST]);
    	TermProject.dataOutputStream.flush();
    }
    
}
