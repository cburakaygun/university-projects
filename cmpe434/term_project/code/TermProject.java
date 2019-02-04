import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class TermProject {
	
	static final int SOCKET_NUMBER = 1432;
	
	static final int TASK_ID_MAPPING 		= -1;
	static final int TASK_ID_LOCALIZATION	= -2;
	static final int TASK_ID_BASKETING		= -3;
	
	static final String MAP_FILE_NANE = "termProjectMap.txt";
	
	static int currentTaskID;
	static boolean resetMapping;		// A flag which states if mapping task should be reset
	static boolean resetLocalization;	// A flag which states if localization (and basketing) task should be reset
	
	static boolean basketingCanWork = false; // A flag which states if the basketing task can begin (i.e, localization was successful)
    
    static DataOutputStream dataOutputStream;
    
    public static void main(String[] args) throws IOException {
    	Robot.writeToLCD("Waiting...");
		
		Sound.beep();
		
		ServerSocket serverSocket = new ServerSocket(SOCKET_NUMBER);
		Socket client = serverSocket.accept();		
		dataOutputStream = new DataOutputStream(client.getOutputStream());

		Robot.writeToLCD("Connected!");
		
		Robot.initiliaze();
		
        Sound.twoBeeps();
        
        Behavior idleBehavior = new Behavior() {			
			@Override
			public boolean takeControl() {
				return Button.ENTER.isDown();
			}
			
			@Override
			public void suppress() {				
			}
			
			@Override
			public void action() {
				Sound.twoBeeps();
				Robot.reset();
			}
		};
        
        Behavior resetBehavior = new Behavior() {			
			@Override
			public boolean takeControl() {
				return Button.ESCAPE.isDown();
			}
			
			@Override
			public void suppress() {				
			}
			
			@Override
			public void action() {
				Sound.twoBeeps();
				if (TermProject.currentTaskID == TASK_ID_MAPPING) {
					resetMapping = true;
				}
				else if (TermProject.currentTaskID == TASK_ID_LOCALIZATION) {
					resetLocalization = true;
				}
			}
		};
		
		Arbitrator arbitrator = new Arbitrator(new Behavior[] {
				new Basketing(),
				new Localization(),
				new Mapping(),
				resetBehavior,
				idleBehavior
		});
		
		arbitrator.go();
        
        serverSocket.close();
    }

}
