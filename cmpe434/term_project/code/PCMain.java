import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;

public class PCMain {
	
	private static InputStream inputStream;
	
	private static DataInputStream dataInputStream;
	
	private static PCMonitor monitor;
	
    public static void main(String[] args) throws UnknownHostException, IOException {    	
    	monitor = new PCMonitor();
        monitor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        monitor.repaint();
        
    	String ip = "10.0.1.81";		
		@SuppressWarnings("resource")
		Socket socket = new Socket(ip, TermProject.SOCKET_NUMBER);
		System.out.println("Connected!");
 
		inputStream = socket.getInputStream();
		dataInputStream = new DataInputStream(inputStream);
		
		while (true) {
			int operationID = dataInputStream.readInt();
			
			if (operationID == TermProject.TASK_ID_MAPPING) {
				System.out.println("MAPPING STARTED");
				monitor.taskID = TermProject.TASK_ID_MAPPING;
				handleMappingData();
				System.out.println("MAPPING DONE");
			}
			else if (operationID == TermProject.TASK_ID_LOCALIZATION) {
				System.out.println("LOCALIZATION STARTED");
				monitor.taskID = TermProject.TASK_ID_LOCALIZATION;
				handleLocalizationData();
				System.out.println("LOCALIZATION DONE");
			}
			else if (operationID == TermProject.TASK_ID_BASKETING) {
				System.out.println("BASKETING STARTED");
				monitor.taskID = TermProject.TASK_ID_BASKETING;
				handleBasketingData();
				System.out.println("BASKETING DONE");
			}
		}
    }
    
    // Gets mapping data from the Robot and updates the PC Map accordingly.
    static void handleMappingData() throws IOException {
    	Cell.resetCells();
    	
    	while (true) {
			int robotIndex = dataInputStream.readInt();
			
			if (robotIndex == TermProject.TASK_ID_MAPPING) { // Mapping process is done
				return;
			}
			
			if (Cell.cells[robotIndex] == null) {
				Cell.cells[robotIndex] = new Cell();
			}
			Cell currentCell = Cell.cells[robotIndex];
			
			monitor.robotIndex = robotIndex;
			monitor.robotDirection = dataInputStream.readInt();
			
			currentCell.color 						= dataInputStream.readInt();
			currentCell.neighbors[Direction.NORTH]	= dataInputStream.readInt();
			currentCell.neighbors[Direction.EAST]	= dataInputStream.readInt();
			currentCell.neighbors[Direction.SOUTH]	= dataInputStream.readInt();
			currentCell.neighbors[Direction.WEST]	= dataInputStream.readInt();
			
			monitor.repaint();
    	}
    }
    
    // Gets localization data from the Robot and updates the PC Map accordingly.
    static void handleLocalizationData() throws IOException {
    	Cell.resetCells();
    	
    	// Gets current map from the Robot
		int cellCount = dataInputStream.readInt();
		
		// Sets up the PC map
		for (int i=0; i<cellCount; i++) {
			int cellIndex = dataInputStream.readInt();
			if (Cell.cells[cellIndex] == null) {
				Cell.cells[cellIndex] = new Cell();
			}
			Cell currentCell = Cell.cells[cellIndex];
			
			currentCell.color 						= dataInputStream.readInt();
			currentCell.neighbors[Direction.NORTH]	= dataInputStream.readInt();
			currentCell.neighbors[Direction.EAST]	= dataInputStream.readInt();
			currentCell.neighbors[Direction.SOUTH]	= dataInputStream.readInt();
			currentCell.neighbors[Direction.WEST]	= dataInputStream.readInt();
		}
		
		// Gets information about Particles and updates PC monitor accordingly
    	while (true) {
			int particleCount = dataInputStream.readInt();
			
			if (particleCount == TermProject.TASK_ID_LOCALIZATION) { // Localization is done
				return;
			}
			
			monitor.particles = new Localization.Particle[particleCount];

			for (int i=0; i<particleCount; i++) {
				int cellIndex = dataInputStream.readInt();
				int direction = dataInputStream.readInt();
				monitor.particles[i] = new Localization.Particle(cellIndex, direction);
			}
			
			monitor.repaint();
    	}
	}
    
    // Gets basketing data from the Robot and updates the PC Map accordingly.
    static void handleBasketingData() throws IOException {
    	while (true) {
			int robotIndex = dataInputStream.readInt();
			
			if (robotIndex == TermProject.TASK_ID_BASKETING) { // Basketing is done
				return;
			}
			
			monitor.robotIndex = robotIndex;
			monitor.robotDirection = dataInputStream.readInt();
			
			monitor.repaint();
    	}
	}
    
}
