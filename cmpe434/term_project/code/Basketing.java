import java.io.IOException;

import lejos.hardware.Sound;
import lejos.robotics.subsumption.Behavior;

public class Basketing implements Behavior {

	private boolean suppressed;

	@Override
	public boolean takeControl() {
		return TermProject.basketingCanWork;
	}

	@Override
	public void action() {
		TermProject.basketingCanWork = false;
		Sound.twoBeeps();
		suppressed = false;
    	
    	if (Cell.indexOfGreenCell == -1) { // GREEN cell not found
    		Sound.buzz();
    		Robot.writeToLCD("NO Gr CELL");
    		return;
    	}
    	
    	int[] pathToGreenCell = PathPlanning.findPath(Robot.CURRENT_INDEX, Cell.indexOfGreenCell, new int[] {Cell.indexOfRedCell, Cell.indexOfBlueCell});
    	if (pathToGreenCell == null) { // No path to GREEN cell is found
    		Sound.buzz();
    		Robot.writeToLCD("NO PATH TO Gr");
    		return;
    	}
    	
    	try {
			TermProject.dataOutputStream.writeInt(TermProject.TASK_ID_BASKETING);
	    	TermProject.dataOutputStream.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	
    	// Moves the Robot to the neighbor of the GREEN cell
    	for (int i=1; i<pathToGreenCell.length-1; i++) { // First cell is the Robot's current cell, last cell is the GREEN cell
    		if (suppressed) {
    			break;
    		}
    		
    		Robot.goToNeighborCell(pathToGreenCell[i]);
    		try {
				sendRobotData();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    	// One iteration is for RED ball, one iteration is for BLUE ball
    	mainLoop: for (int i=0; i<2; i++) {
    		if (suppressed) {
    			break mainLoop;
    		}
    		
        	Robot.rotateToDirection(Robot.findNeighborDirection(Cell.indexOfGreenCell));
        	try {
				sendRobotData();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        	
        	Robot.moveForwardUntilColor(Colour.GREEN);
    		
        	int prevIndex = Robot.CURRENT_INDEX;
        	Robot.CURRENT_INDEX = Cell.indexOfGreenCell;
        	try {
				sendRobotData();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        	
        	int ballColor = Robot.graspBall();
        	
        	Robot.moveBackwardWhileColor(Colour.GREEN);
        	Robot.travel(20-Robot.TRAVEL_AMOUNT); // Moves the Robot to the (almost) center of the white cell
        	
        	Robot.CURRENT_INDEX = prevIndex;
        	try {
				sendRobotData();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        	
        	// HANDLE BASKETING BALL
        	int destCellIndex = (ballColor == Colour.RED) ? Cell.indexOfRedCell : Cell.indexOfBlueCell;
        	
        	if (destCellIndex == -1) { // RED/BLUE cell not found
        		Sound.buzz();
        		Robot.writeToLCD("NO " + (ballColor == Colour.RED ? "RED" : "BLUE") + " CELL");
        		break mainLoop;
        	}
        	
        	int[] cellsToAvoid = new int[] {
        		Cell.indexOfGreenCell,
        		(ballColor == Colour.RED) ? Cell.indexOfBlueCell : Cell.indexOfRedCell
        	};
        	
        	int[] pathToDest = PathPlanning.findPath(Robot.CURRENT_INDEX, destCellIndex, cellsToAvoid);
        	if (pathToDest == null) { // No path to RED/BLUE cell is found
        		Sound.buzz();
        		Robot.writeToLCD("NO PATH TO " + (ballColor == Colour.RED ? "RED" : "BLUE"));
        		break mainLoop;
        	}
        	
        	// Moves the Robot to the neighbor of the RED/BLUE cell
        	for (int j=1; j<pathToDest.length-1; j++) { // First cell is the Robot's current cell, last cell is the RED/BLUE cell
        		if (suppressed) {
        			break mainLoop;
        		}
        		
        		Robot.goToNeighborCell(pathToDest[j]);
        		try {
					sendRobotData();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
        	
        	Robot.rotateToDirection(Robot.findNeighborDirection(destCellIndex));
        	try {
				sendRobotData();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
        	Robot.moveForwardUntilColor(ballColor);
        	Robot.releaseBall();
        	Robot.moveBackwardWhileColor(ballColor);
        	Robot.travel(22-Robot.TRAVEL_AMOUNT); // Moves the Robot to the (almost) center of the white cell
        	
        	// Moves the Robot to its starting cell (the neighbor of the GREEN cell)
        	for (int j=pathToDest.length-3; j>=0; j--) {
        		if (suppressed) {
        			break mainLoop;
        		}
        		
        		Robot.goToNeighborCell(pathToDest[j]);
        		try {
					sendRobotData();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
    	}
    	
    	if (!suppressed) {
    		Robot.rotateToDirection(Robot.findNeighborDirection(Cell.indexOfGreenCell));
    		try {
    			sendRobotData();
    	    	Robot.travel(Robot.TRAVEL_AMOUNT);
    	    	Robot.CURRENT_INDEX = Cell.indexOfGreenCell;
    	    	sendRobotData();
    	    	Robot.beep3Times();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	
    	try {
			TermProject.dataOutputStream.writeInt(TermProject.TASK_ID_BASKETING);
	    	TermProject.dataOutputStream.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void suppress() {
		suppressed = true;
	}

	// Sends the following information to the socket in order:
	// - The index of the cell on which the Robot currently stands
	// - The direction of the Robot
    private  void sendRobotData() throws IOException {
    	TermProject.dataOutputStream.writeInt(Robot.CURRENT_INDEX);
    	TermProject.dataOutputStream.flush();
    	
    	TermProject.dataOutputStream.writeInt(Robot.ROBOT_DIR);
    	TermProject.dataOutputStream.flush();
    }

}
