import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.robotics.subsumption.Behavior;

/**
 * First, 4 Particles with distinct directions are created for each WHITE cell.
 * A Particle represents the Robot's possible current cell and its direction in that cell.
 * 
 * Then, cells are examined by moving the Robot in a DFS manner and Particles are eliminated accordingly
 * until only 1 Particle is left.
 * 
 * As the cells are examined, modifies `particles` and sends data to PC over socket.
 * Assigns the initial direction of the Robot as NORTH.
 *
 */
public class Localization implements Behavior {
	
	Particle[] particles;
	int particleCount; // Number of Particles (non-null elements) of `particles`
	private boolean suppressed = false;
	
	@Override
	public boolean takeControl() {
		return (Button.DOWN.isDown() || TermProject.resetLocalization);
	}

	@Override
	public void action() {
		Sound.twoBeeps();
		TermProject.resetLocalization = false;
		suppressed = false;
		Robot.liftLeverUp();
		
		TermProject.currentTaskID = TermProject.TASK_ID_LOCALIZATION;
		
		//INIT
		loadMap();    	
		initializeParticles();
		
		try {
			TermProject.dataOutputStream.writeInt(TermProject.TASK_ID_LOCALIZATION);
	    	TermProject.dataOutputStream.flush();
	    	
	    	sendMapToPC();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// WORK
		Robot.CURRENT_INDEX = -1;
		Robot.ROBOT_DIR = Direction.NORTH;
		Robot.rotateUltrasonicToRobotDirection();
		
		mainLoop: while (true) {
			if (suppressed) {
				break;
			}
			
			Cell currentCell = new Cell();
		
			Cell.setColor(currentCell);
			filterParticlesByColor(currentCell.color); // Removes the Particles that currently are NOT on a cell with the given color.
			
			try {
				sendData();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (particleCount <= 1) {
				finishLocalization();
				break mainLoop;
			}
			
			// Examines the for side of the current cell and eliminated Particles according to the presence of a WALL
			for (int i=0; i<4; i++) {
				
				if (suppressed) {
					break mainLoop;
				}
				
				int directionAmount = (Robot.ULTRASONIC_DIR-Robot.ROBOT_DIR+4)%4;
				
				if (Robot.isWallPresent()) {
					currentCell.neighbors[Robot.ULTRASONIC_DIR] = Cell.WALL;
					filterParticlesByWall(directionAmount, true);
				} else {
					filterParticlesByWall(directionAmount, false);
				}				
			
				try {
					sendData();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				if (particleCount <= 1) {
					finishLocalization();
					break mainLoop;
				}
			
				if (i != 3) {
					Robot.rotateUltrasonicLeft();
				}
			}
			
			Robot.rotateUltrasonicToRobotDirection();
			
			int direction = -1; // Direction to travel to reach a neighbor cell
			for (int i=0; i<4; i++) {
				if (suppressed) {
					break mainLoop;
				}

				if (currentCell.neighbors[(Robot.ROBOT_DIR+i)%4] == Cell.WALL) {
					continue;
				}
					
				direction = (Robot.ROBOT_DIR+i)%4;
				
				int directionAmount = (direction-Robot.ROBOT_DIR+4)%4;
				
				currentCell.neighbors[direction] = Cell.WALL; // Assigns a WALL to this side to prevent the Robot from going to the same neighbor if it comes back to the current cell
				
				Robot.rotateToDirection(direction);
				Robot.ULTRASONIC_DIR = Robot.ROBOT_DIR;
				rotateParticles(directionAmount);
				try {
					sendData();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				Robot.travel(Robot.TRAVEL_AMOUNT-15);
				
				int cellColor = Robot.getCellColor();
				if (cellColor != Colour.WHITE) {
					Robot.travel(-1 * (Robot.TRAVEL_AMOUNT-15));
					
					filterParticlesByNeighborColor(cellColor); // Removes the Particles which DO NOT point to a neighbor cell with the given color.
					
					try {
						sendData();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (particleCount <= 1) {
						finishLocalization();
						break mainLoop;
					}
				}
				else {
					Robot.travel(Robot.TRAVEL_AMOUNT-18);
					moveParticlesToNextCell();
					try {
						sendData();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					break;
				}
			}
			
			if (direction == -1) { // Couldn't find a neighbor to visit
				finishLocalization();
				break mainLoop;
			}
		}
		
		if (!suppressed) {
			Robot.beep3Times();
			
	    	if (Robot.CURRENT_INDEX == -1) { // Localization failed
	    		TermProject.basketingCanWork = false;
	    		Sound.buzz();
	    		Robot.writeToLCD("LOCAL FAIL");
	    	} else {
	    		TermProject.basketingCanWork = true;
	    	}
		} else {
			TermProject.basketingCanWork = false;
		}
		
		try {
			TermProject.dataOutputStream.writeInt(TermProject.TASK_ID_LOCALIZATION);
	    	TermProject.dataOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void suppress() {
		suppressed = true;
	}
	
	// Removes the Particles that currently are NOT on a cell with the given `color`.
	private void filterParticlesByColor(int color) {
		for (int i=0; i<particles.length; i++) {
			if (particles[i] != null) {
				if (Cell.cells[particles[i].cellIndex].color != color) {
					particles[i] = null;
					particleCount--;
				}
			}
		}
	}

	// Removes the Particles according to the presence of a WALL on the side <Particle's direction> + `directioAmount`.
	// If `keepIfWall` is true, removes the Particles which do NOT point to a WALL.
	// Else, removes the Particles which DO point a WALL.
	private void filterParticlesByWall(int directioAmount, boolean keepIfWall) {
		for (int i=0; i<particles.length; i++) {
			if (particles[i] != null) {
				int directionToLookAt = (particles[i].direction+directioAmount)%4;
				if ((Cell.cells[particles[i].cellIndex].neighbors[directionToLookAt] == Cell.WALL) != keepIfWall) {
					particles[i] = null;
					particleCount--;
				}
			}
		}
	}
	
	// Removes the Particles which DO NOT point to a neighbor cell with the given `color`.
	private void filterParticlesByNeighborColor(int color) {
		for (int i=0; i<particles.length; i++) {
			if (particles[i] != null) {
				int neighborIndex = Cell.cells[particles[i].cellIndex].neighbors[particles[i].direction];
				if (Cell.cells[neighborIndex].color != color) {
					particles[i] = null;
					particleCount--;
				}
			}
		}
	}

	// Rotates all the Particles by given `directionAmount`.
	private void rotateParticles(int directionAmount) {
		if (directionAmount == 0) {
			return;
		}
		
		for (int i=0; i<particles.length; i++) {
			if (particles[i] != null) {
				particles[i].direction = (particles[i].direction+directionAmount+4)%4;
			}
		}
	}
	
	// Moves all the Particles to the next cell in their current directions.
	private void moveParticlesToNextCell() {		
		for (int i=0; i<particles.length; i++) {
			Particle particle = particles[i];
			if (particle != null) {				
				if (particle.direction == Direction.NORTH) {
					particle.cellIndex -= 7;
				}
				else if (particle.direction == Direction.EAST) {
					particle.cellIndex += 1;
				}
				else if (particle.direction == Direction.SOUTH) {
					particle.cellIndex += 7;
				}
				else if (particle.direction == Direction.WEST) {
					particle.cellIndex -= 1;
				}
			}
		}
	}
	
	// Sets the current cell and the direction of the Robot to the cell and the direction of the remaning Particle.
	private void finishLocalization() {
		Robot.rotateUltrasonicToRobotDirection();
		
		if (particleCount == 1) { // Localization is successful
			for(Particle particle : particles) {
				if (particle != null) {
					Robot.CURRENT_INDEX = particle.cellIndex;
					Robot.ROBOT_DIR = particle.direction;
					Robot.ULTRASONIC_DIR = Robot.ROBOT_DIR;	
					return;
				}
			}
		}
	}

	// Sends the following information to the socket in order:
	// - Number of Particles left
	// - For each Particle:
	// 	- Index of the cell the Particle is currently stands on
	// 	- Direction of the Particle in that cell
    private void sendData() throws IOException {
    	TermProject.dataOutputStream.writeInt(particleCount);
    	TermProject.dataOutputStream.flush();
    	
    	for (Particle particle : particles) {
    		if (particle != null) {
            	TermProject.dataOutputStream.writeInt(particle.cellIndex);
            	TermProject.dataOutputStream.flush();
            	
            	TermProject.dataOutputStream.writeInt(particle.direction);
            	TermProject.dataOutputStream.flush();
    		}
    	}
    }

	// Reads map from the file
	private void loadMap() {
    	if (Cell.cells == null) {
			try {
				FileInputStream fileInputStream = new FileInputStream(new File(TermProject.MAP_FILE_NANE));
				ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
				Cell.cells = (Cell[]) objectInputStream.readObject();
				objectInputStream.close();
				fileInputStream.close();
		    	Cell.analyzeCells();
			} catch (IOException | ClassNotFoundException e) {
				Sound.buzz();
				Robot.writeToLCD("MAP NOT FOUND");
				return;
			}
    	}
	}
	
	private void initializeParticles() {
		particles = new Particle[4*Cell.whiteCellCount];
		particleCount = 0;
		
		for (int i=0; i<Cell.cells.length; i++) {
			if (Cell.cells[i] != null && Cell.cells[i].color == Colour.WHITE) { // Creates 4 particles for each WHITE cell
				particles[particleCount]   = new Particle(i, Direction.NORTH);
				particles[particleCount+1] = new Particle(i, Direction.EAST);
				particles[particleCount+2] = new Particle(i, Direction.SOUTH);
				particles[particleCount+3] = new Particle(i, Direction.WEST);
				particleCount += 4;
			}
		}
	}
	
	// Sends current map to the PC
	private void sendMapToPC() throws IOException {
    	TermProject.dataOutputStream.writeInt(Cell.cellCount);
    	TermProject.dataOutputStream.flush();
    	
    	for (int i=0; i<Cell.cells.length; i++) {
    		Cell cell = Cell.cells[i];
    		if (cell != null) {
            	TermProject.dataOutputStream.writeInt(i);
            	TermProject.dataOutputStream.flush();
            	
            	TermProject.dataOutputStream.writeInt(cell.color);
            	TermProject.dataOutputStream.flush();
            	
            	TermProject.dataOutputStream.writeInt(cell.neighbors[Direction.NORTH]);
            	TermProject.dataOutputStream.flush();
            	
            	TermProject.dataOutputStream.writeInt(cell.neighbors[Direction.EAST]);
            	TermProject.dataOutputStream.flush();
            	
            	TermProject.dataOutputStream.writeInt(cell.neighbors[Direction.SOUTH]);
            	TermProject.dataOutputStream.flush();
            	
            	TermProject.dataOutputStream.writeInt(cell.neighbors[Direction.WEST]);
            	TermProject.dataOutputStream.flush();
    		}
    	}
	}
	
    /**
     * A Particle represents the Robot's possible current cell and its direction in that cell.
     *
     */
	static class Particle {
		int cellIndex;
		int direction;
		
		Particle(int cellIndex, int direction) {
			this.cellIndex = cellIndex;
			this.direction = direction;
		}
	}
	
}
