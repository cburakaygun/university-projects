import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Line2D;

import javax.swing.JFrame;

public class PCMonitor extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private static final int GRAPH_OFFSET = 100; 					// px; offset from the top-left of the screen
	
	private static final int CELL_SIZE = 200;						// px
	private static final int WALL_STROKE_WIDTH = 6;					// px
    
	private static final int ROBOT_SQUARE_SIZE		= CELL_SIZE/4;	// px
    private static final int PARTICLE_SQUARE_SIZE	= CELL_SIZE/6;	// px
    
    private static final Color ROBOT_COLOR		= Color.ORANGE;
    private static final Color PARTICLE_COLOR	= Color.ORANGE;

	int taskID;
	
	int robotIndex = -1; // Index of the cell the Robot is currently stands on
	int robotDirection = Direction.UNKNOWN; // Current direction of the Robot
	
	Localization.Particle[] particles;
	
    PCMonitor() {
        super("CmpE434 - 2018 - TermProject - Group4");
    	
        setBackground(Color.WHITE);
        setSize(7*CELL_SIZE + 2*GRAPH_OFFSET, 7*CELL_SIZE + 2*GRAPH_OFFSET);
        setVisible(true);
    }
    
    public void paint(Graphics g) {
        super.paint(g);

        paintCells(g);
        
        if (taskID == TermProject.TASK_ID_MAPPING || taskID == TermProject.TASK_ID_BASKETING) {
            paintRobot(g);
        }
        else if (taskID == TermProject.TASK_ID_LOCALIZATION) {
        	paintParticles(g);
        }
    }
    
    // Paints 7x7 grid (Only actual map (4x4 grid) is visible)
    private void paintCells(Graphics g) {
    	if (Cell.cells == null) {
    		return;
    	}
    	
    	Graphics2D g2 = (Graphics2D) g;

    	// PAINTS CELL COLORS
        g2.setStroke(new BasicStroke(0));
        for (int row=0; row<7; row++) {
            for (int col=0; col<7; col++) {
                Cell cell = Cell.cells[row*7 + col];

                if (cell != null) {
                	int cellX = GRAPH_OFFSET + col*CELL_SIZE;
                	int cellY = GRAPH_OFFSET + row*CELL_SIZE;
                
                	g2.setColor(cellColortoAWTColor(cell.color));
                	g2.fillRect(cellX, cellY, CELL_SIZE, CELL_SIZE);
                }
            }
        }

        // PAINTS CELL WALLS
        g2.setColor(Color.CYAN);
        g2.setStroke(new BasicStroke(WALL_STROKE_WIDTH));
        for (int row=0; row<7; row++) {
            for (int col = 0; col < 7; col++) {
                Cell cell = Cell.cells[row*7 + col];

                if (cell != null) {
	                int cellX = GRAPH_OFFSET + col*CELL_SIZE;
	                int cellY = GRAPH_OFFSET + row*CELL_SIZE;
	
	                if (cell.neighbors[Direction.NORTH] == Cell.WALL) {
	                    g2.draw(new Line2D.Double(cellX, cellY, cellX+CELL_SIZE, cellY));
	                }
	                if (cell.neighbors[Direction.EAST] == Cell.WALL) {
	                    g2.draw(new Line2D.Double(cellX+CELL_SIZE, cellY, cellX+CELL_SIZE, cellY+CELL_SIZE));
	                }
	                if (cell.neighbors[Direction.SOUTH] == Cell.WALL) {
	                    g2.draw(new Line2D.Double(cellX, cellY+CELL_SIZE, cellX+CELL_SIZE, cellY+CELL_SIZE));
	                }
	                if (cell.neighbors[Direction.WEST] == Cell.WALL) {
	                    g2.draw(new Line2D.Double(cellX, cellY, cellX, cellY+CELL_SIZE));
	                }
                }
            }
        }
    }
    
    // Paints the Robot onto its current cell with its current direction
    private void paintRobot(Graphics g) {    	
    	if (robotIndex != -1) {
        	Graphics2D g2 = (Graphics2D) g;
            g2.setColor(ROBOT_COLOR);
            g2.setStroke(new BasicStroke(0));
            
            int cellX = GRAPH_OFFSET + (robotIndex%7)*CELL_SIZE;
            int cellY = GRAPH_OFFSET + (robotIndex/7)*CELL_SIZE;
            
            int robotX = cellX + (CELL_SIZE-ROBOT_SQUARE_SIZE)/2;	// Middle of the cell
            int robotY = cellY + (CELL_SIZE-ROBOT_SQUARE_SIZE)/2;	// Middle of the cell
            
            g2.fillPolygon(getTriangle(robotDirection, robotX, robotY, ROBOT_SQUARE_SIZE));
        }
    }
    
    // Paints particles onto their current cells with their current directions
    private void paintParticles(Graphics g) {    	
    	if (particles == null) {
    		return;
    	}

    	Graphics2D g2 = (Graphics2D) g;
    	g2.setColor(PARTICLE_COLOR);
        g2.setStroke(new BasicStroke(0));
    	
    	for (Localization.Particle particle : particles) {
            int cellX = GRAPH_OFFSET + (particle.cellIndex%7)*CELL_SIZE;
            int cellY = GRAPH_OFFSET + (particle.cellIndex/7)*CELL_SIZE;
            
            int particleX = cellX + (CELL_SIZE-PARTICLE_SQUARE_SIZE)/2;	// Middle of the cell
            int particleY = cellY + (CELL_SIZE-PARTICLE_SQUARE_SIZE)/2;	// Middle of the cell
            
            int offset = (CELL_SIZE+PARTICLE_SQUARE_SIZE)/4;
            if (particle.direction == Direction.NORTH) {
            	particleY -= offset;
            }
            else if (particle.direction == Direction.EAST) {
            	particleX += offset;
            }
            else if (particle.direction == Direction.SOUTH) {
            	particleY += offset;
            }
            else if (particle.direction == Direction.WEST) {
            	particleX -= offset;
            }
            
            g2.fillPolygon(getTriangle(particle.direction, particleX, particleY, PARTICLE_SQUARE_SIZE));
    	}
    }
    
    // Returns a triangle pointing to the given `direction` that fits in a square with side `squareSize`.
    private Polygon getTriangle(int direction, int x, int y, int squareSize) {
    	switch (direction) {
			case Direction.NORTH:
				return new Polygon(
						new int[] {x, x+squareSize/2, x+squareSize, x},
						new int[] {y+squareSize, y, y+squareSize, y+squareSize},
						4
				);
			case Direction.EAST:
				return new Polygon(
						new int[] {x, x+squareSize, x, x},
						new int[] {y, y+squareSize/2, y+squareSize, y},
						4
				);
			case Direction.SOUTH:
				return new Polygon(
						new int[] {x, x+squareSize/2, x+squareSize, x},
						new int[] {y, y+squareSize, y, y},
						4
				);
			case Direction.WEST:
				return new Polygon(
						new int[] {x+squareSize, x, x+squareSize, x+squareSize},
						new int[] {y, y+squareSize/2, y+squareSize, y},
						4
				);
    	}
    	return null;
    }
    
    private java.awt.Color cellColortoAWTColor(int cellColor) {    	
        switch (cellColor) {
            case Colour.WHITE:
                return java.awt.Color.WHITE;
            case Colour.BLACK:
                return java.awt. Color.BLACK;
            case Colour.RED:
                return java.awt.Color.RED;
            case Colour.GREEN:
                return java.awt.Color.GREEN;
            case Colour.BLUE:
                return java.awt.Color.BLUE;
            default:
            	return java.awt.Color.LIGHT_GRAY;
        }
    }
    
}
