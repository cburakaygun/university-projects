import java.io.Serializable;

/**
 * A Cell represents a grid cell of the actual map.
 * Each Cell stores the information of color and its 4 neighbors.
 * 
 * This class also has a static `cells` array which stores the cells of the actual map.
 */
public class Cell implements Serializable {
	private static final long serialVersionUID = 2L;

	// Neighbors
	static final int WALL 		= -1;
	static final int UNKNOWN	= -2;
	
	static Cell[] cells; // 7x7 grid (actual map is a 4x4 grid)
	static int cellCount; // Number of discovered cells
	static int whiteCellCount; // Number of discovered WHITE cells
	static int indexOfRedCell;
	static int indexOfGreenCell;
	static int indexOfBlueCell;
	
	static void resetCells() {
		cells = new Cell[49];
		cellCount = whiteCellCount = 0;
		indexOfRedCell = indexOfGreenCell = indexOfBlueCell = -1;
	}
	
	static void analyzeCells() {
		cellCount = whiteCellCount = 0;
		indexOfRedCell = indexOfGreenCell = indexOfBlueCell = -1;
		
		for (int i=0; i<cells.length; i++) {
			Cell cell = cells[i];
			
			if (cell != null) {
				cellCount++;
				
				if (cell.color == Colour.WHITE) {
					whiteCellCount++;
				}
				else if (cell.color == Colour.RED) {
					indexOfRedCell = i;
				}
				else if (cell.color == Colour.GREEN) {
					indexOfGreenCell = i;
				}
				else if (cell.color == Colour.BLUE) {
					indexOfBlueCell = i;
				}
			}
		}
	}
	
	// Sets the color of given `cell` according to the color sensor value. 
    static void setColor(Cell cell) {
    	cell.color = Robot.getCellColor();
    }
    
    // Instance fields
    int color;
    int[] neighbors = new int[4]; // Stores WALL, UNKONWN or the index (0-48) of the neighbor cell
    
    Cell() {
    	this.color = Colour.UNKNOWN;
    	this.neighbors[Direction.NORTH]	= UNKNOWN;
    	this.neighbors[Direction.EAST]	= UNKNOWN;
    	this.neighbors[Direction.SOUTH]	= UNKNOWN;
    	this.neighbors[Direction.WEST]	= UNKNOWN;
    }
    
    Cell(int color, int northNeighbor, int eastNeighbor, int southNeighbor, int westNeighbor) {
    	this.color = color;
    	this.neighbors[Direction.NORTH]	= northNeighbor;
    	this.neighbors[Direction.EAST]	= eastNeighbor;
    	this.neighbors[Direction.SOUTH]	= southNeighbor;
    	this.neighbors[Direction.WEST]	= westNeighbor;
    }
    
    // Test data
    static void setUpDefaultCells() {
    	resetCells();
    	//cells[9]  = new Cell(Colour.WHITE, Cell.WALL, 10, 16, Cell.WALL);
		//cells[10] = new Cell(Colour.WHITE, Cell.WALL, Cell.WALL, Cell.WALL, 9);		
	    cells[11] = new Cell(Colour.GREEN, Cell.WALL, Cell.WALL, 18, Cell.WALL);	    
	    cells[12] = new Cell(Colour.BLUE, Cell.WALL, Cell.WALL, 19, Cell.WALL);
	    
	    //cells[16] = new Cell(Colour.WHITE, 9, Cell.WALL, Cell.WALL, Cell.WALL);
	    cells[17] = new Cell(Colour.WHITE, Cell.WALL, 18, 24, Cell.WALL);
	    cells[18] = new Cell(Colour.WHITE, 11, Cell.WALL, 25, 17);
	    cells[19] = new Cell(Colour.WHITE, 12, Cell.WALL, 26, Cell.WALL);
	    
	    cells[23] = new Cell(Colour.WHITE, Cell.WALL, 24, 30, Cell.WALL);
	    cells[24] = new Cell(Colour.WHITE, 17, Cell.WALL, Cell.WALL, 23);
	    cells[25] = new Cell(Colour.WHITE, 18, 26, 32, Cell.WALL);	    
	    cells[26] = new Cell(Colour.WHITE, 19, Cell.WALL, Cell.WALL, 25);
	    
	    cells[30] = new Cell(Colour.BLACK, 23, 31, Cell.WALL, Cell.WALL);
	    cells[31] = new Cell(Colour.WHITE, Cell.WALL, Cell.WALL, Cell.WALL, 30);
	    cells[32] = new Cell(Colour.RED, 25, Cell.WALL, Cell.WALL, Cell.WALL);
	    analyzeCells();
    }
    
}
