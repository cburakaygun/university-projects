import java.util.LinkedList;
import java.util.Queue;

/**
 * This static class is responsible for finding the path between two given cells (indices)
 * while avoiding BLACK cells as well as some other specified cells given as parameter.
 * 
 * Algorithm is a modified version of Breadth First Search (BFS) which uses `Cell.cells` as input graph.
 */
public class PathPlanning {
	
	// Returns an Array which contains the Cells (indices) of the path from `sourceIndex` to `destIndex`.
	// (`sourceIndex` and `destIndex` are included in the result.)
	// Returns null if no path is found.
	//
	// avoidIndices	: Indices of the Cells to be avoided while constructing the path. (BLACK Cells are avoided automatically.)
	static int[] findPath(int sourceIndex, int destIndex, int[] avoidIndices) {
		Queue<Integer> queue = new LinkedList<Integer>();
		
		int[] precedences = new int[Cell.cells.length];		// Contains the immediate precedence of each Cell of the path from `sourceIndex` to `destIndex`
		boolean[] visited = new boolean[Cell.cells.length];	// Flags to show if a Cell was visited before while constructing the path or not
		
		for (int i=0; i<Cell.cells.length; i++) {
			visited[i] = false;	
			precedences[i] = -1;
		}
		
		queue.add(sourceIndex);
		visited[sourceIndex] = true;
		
		while (!queue.isEmpty()) {
			int cellIndex = (int) queue.poll();
			Cell cell = Cell.cells[cellIndex];
			
			for (int i=0; i<4; i++) {
				int neighborIndex = cell.neighbors[i];
				
				if (neighborIndex != Cell.WALL &&
					Cell.cells[neighborIndex].color != Colour.BLACK &&
					!arrayContains(avoidIndices, neighborIndex)
				){	
					if (!visited[neighborIndex]) {
						visited[neighborIndex] = true;
						precedences[neighborIndex] = cellIndex;
						
						if (neighborIndex == destIndex) {
							queue.clear();	// This is done to "break" the while loop
							break; 			// Breaks the for loop
						}
						
						queue.add(neighborIndex);
					}
				}
			}
		}
		
		if (precedences[destIndex] == -1) { // Path not found
			return null;
		}
		
		// Calculates the length of the found path
		int pathLength = 1;
		int precedence = precedences[destIndex];
		while (precedence != sourceIndex) {
			pathLength++;
			precedence = precedences[precedence];
		}
		
		int[] pathCells = new int[pathLength+1];
		pathCells[pathCells.length-1] = destIndex;
		
		int pathIndex = pathCells.length-2;
		precedence = precedences[destIndex];
		while (pathIndex >= 0) {
			pathCells[pathIndex] = precedence;
			pathIndex--;
			precedence = precedences[precedence];
		}
		
		return pathCells;
	}
	
	// Returns true if `arr` contains given `element`. Else, returns false.
	private static boolean arrayContains(int[] arr, int element) {
		if (arr == null) {
			return false;
		}
		
		for (int e : arr) {
			if (e == element) {
				return true;
			}
		}
		
		return false;
	}
	
}
