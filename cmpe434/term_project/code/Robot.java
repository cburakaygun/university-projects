import lejos.hardware.BrickFinder;
import lejos.hardware.Sound;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.motor.NXTRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.NXTLightSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;
import lejos.utility.Delay;

/**
 * This class represents the Robot.
 * To move the Robot and get information from the Robot sensors, the methods provided by this class are used.
 * 
 * This class also stores the following information about the Robot and updates them according to its movements.
 * - Index of the current Cell the Robot is currently stands on
 * - Current (forward) direction of the Robot
 * - Current (forward) direction of the Ultrasonic sensor
 *
 */
public class Robot {
	
	static EV3 ev3 = (EV3) BrickFinder.getDefault();
	
	static GraphicsLCD graphicsLCD = ev3.getGraphicsLCD();
 
	static EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.A);
	static EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.D);
	static NXTRegulatedMotor ultrasonicMotor = new NXTRegulatedMotor(MotorPort.B);
	static EV3MediumRegulatedMotor leverMotor = new EV3MediumRegulatedMotor(MotorPort.C);
 
	static EV3ColorSensor ev3ColorSensor = new EV3ColorSensor(SensorPort.S1);
	static SampleProvider ev3ColorSampleProvider = ev3ColorSensor.getRGBMode();
	static float[] ev3ColorSamples = new float[ev3ColorSampleProvider.sampleSize()];
 
	static NXTLightSensor nxtLightSensor = new NXTLightSensor(SensorPort.S2);
	static SampleProvider nxtLightSampleProvider = nxtLightSensor.getRedMode();
	static float[] nxtLightSamples = new float[nxtLightSampleProvider.sampleSize()];
	
	static EV3UltrasonicSensor ultrasonicSensor = new EV3UltrasonicSensor(SensorPort.S3);
	static SampleProvider ultrasonicSampleProvider = ultrasonicSensor.getDistanceMode();
	static float[] ultrasonicSamples = new float[ultrasonicSampleProvider.sampleSize()];
 
	static EV3GyroSensor gyroSensor = new EV3GyroSensor(SensorPort.S4);
	static SampleProvider gyroSampleProvider = gyroSensor.getAngleMode();
	static float[] gyroSamples = new float[gyroSampleProvider.sampleSize()];
	
	static double leftWheelDiameter = 5.6;
	static double rightWheelDiameter = 5.6;
	static double trackWidth = 10.85;
	static MovePilot pilot;
	
    static int ROBOT_DIR;		// Current (forward) direction of the Robot
    static int ULTRASONIC_DIR;  // Current (forward) direction of the Ultrasonic sensor
    
    static int CURRENT_INDEX;	// Index of the current Cell the Robot is currently stands on
    
    static int TRAVEL_AMOUNT = 34; // The distance in cm to move the Robot from cell to cell
	
    // Assumes the direction of the Ultrasonic sensor is the same as the direction of the Robot when this method is invoked.
	static void initiliaze() {
		setUpLeverMotor();
		ultrasonicMotor.resetTachoCount();
		
		ROBOT_DIR = Direction.UNKNOWN;
		ULTRASONIC_DIR = Direction.UNKNOWN;
		
		CURRENT_INDEX = -1;
		
		gyroSensor.reset();
		setUpPilot();
	}
    
	static void reset() {
		pilot.stop();
		ultrasonicMotor.rotateTo(0);		
	}
	
   	// Sets up the lifting mechanism (grasping and releasing a ball) and lifts the mechanism up as high as possible.
	private static void setUpLeverMotor() {
		leverMotor.setSpeed(180);
		leverMotor.resetTachoCount();
		liftLeverUp();
	}
	
	private static void setUpPilot() {
   		Chassis chassis = new WheeledChassis(
   				new Wheel[]{
   						WheeledChassis.modelWheel(leftMotor, leftWheelDiameter).offset(-trackWidth/2).invert(false),
   						WheeledChassis.modelWheel(rightMotor, rightWheelDiameter).offset(trackWidth/2).invert(false)
   			},
   				WheeledChassis.TYPE_DIFFERENTIAL
       	);
    
   		pilot = new MovePilot(chassis);
   		
   		pilot.setAngularSpeed(30);
   		pilot.setAngularAcceleration(10);
   		
   		pilot.stop();
   	}
    
   	// Returns the color of the cell the Robot is currently on.
   	static int getCellColor() {
   		ev3ColorSampleProvider.fetchSample(ev3ColorSamples, 0);
    
   		float red	= ev3ColorSamples[0];
   		float green	= ev3ColorSamples[1];
   		float blue	= ev3ColorSamples[2];
   		
   		if (red+green+blue < 0.015) {
   			return Colour.BLACK;
   		}
   		else if (red+green+blue > 0.09) {
   			return Colour.WHITE;
   		}
   		else if (red >= green+blue) {
   			return Colour.RED;
   		}
   		else if (green >= red+blue) {
   			return Colour.GREEN;
   		}
   		else if (blue >= red+green) {
   			return Colour.BLUE;
   		}
   		else {
   			return Colour.UNKNOWN;
   		}
   	}

   	// If there is a WALL in front of Ultrasonic sensor, returns true. Else, returns false.
    static boolean isWallPresent() {
    	int result = 0;
    	
    	for (int i=0; i<4; i++) {
    		Delay.msDelay(250);
        	ultrasonicSampleProvider.fetchSample(ultrasonicSamples, 0);
        	
        	if (ultrasonicSamples[0] < 0.25) { // 25 cm
        		result += 1;
        	} else {
        		result -= 1;
        	}
    	}
    	
    	if (result > 1) {
    		Sound.twoBeeps();
    		return true;
    	} else {
    		return false;
    	}
    }

    // Rotates Ultrasonic sensor 90 degree counterclockwise and updates its direction.
    static void rotateUltrasonicLeft() {
    	ultrasonicMotor.rotate(91);
    	ULTRASONIC_DIR = (ULTRASONIC_DIR+3)%4;
    }

    // Rotates Ultrasonic sensor 90 degree clockwise and updates its direction.
    static void rotateUltrasonicRight() {
    	ultrasonicMotor.rotate(-92);
        ULTRASONIC_DIR = (ULTRASONIC_DIR+1)%4;
    }
    
    // Rotates Ultrasonic sensor to the direction of the Robot and updates its direction.
    static void rotateUltrasonicToRobotDirection() {
    	ultrasonicMotor.rotateTo(0);
    	ULTRASONIC_DIR = ROBOT_DIR;
    }
    
    // Point-rotates the Robot 90 degrees counter clockwise and updates its direction.
   	static void rotateLeft() {
   		gyroSensor.reset();
   		pilot.rotate(-85);
   		Delay.msDelay(50);
   		gyroSampleProvider.fetchSample(gyroSamples, 0);
   		pilot.rotate(gyroSamples[0]-89);
   		
   		addToDirection(3);
   	}
   	
   	// Point-rotates the Robot 90 degrees clockwise and updates its direction.
   	static void rotateRight() {
   		gyroSensor.reset();
   		pilot.rotate(85);
   		Delay.msDelay(50);
   		gyroSampleProvider.fetchSample(gyroSamples, 0);
   		pilot.rotate(89+gyroSamples[0]);
   		
   		addToDirection(1);
   	}
    
   	// Point-rotates the Robot 180 degrees clockwise and updates its direction.
   	static void rotateBack() {
   		gyroSensor.reset();
   		pilot.rotate(175);
   		Delay.msDelay(50);
   		gyroSampleProvider.fetchSample(gyroSamples, 0);
   		pilot.rotate(179+gyroSamples[0]);
   		
   		addToDirection(2);
   	}
   	
   	// Rotates the Robot to given `direction` and updates its direction.
    static void rotateToDirection(int direction) {
    	int directionAmount = (ROBOT_DIR - direction + 4) % 4;
    	
    	if (directionAmount == 1) {
    		rotateLeft();
    	}
    	else if (directionAmount == 2) {
    		rotateBack();
    	}
    	else if (directionAmount == 3) {
    		rotateRight();
    	}
    }
    
    // Moves the Robot to the cell on the map with given `cellIndex` and updates its direction and current cell.
   	static void goToNeighborCell(int neighborIndex) {
   		int neighborDirection = findNeighborDirection(neighborIndex);
   		
   		if (neighborDirection != Direction.UNKNOWN) {
   			rotateToDirection(neighborDirection);
   			travel(TRAVEL_AMOUNT);
   	   		CURRENT_INDEX = neighborIndex;
   		}
   	}
   	
   	// Returns the direction of the neighbor cell stated by `neighborIndex` relative to the current cell of the Robot.
   	static int findNeighborDirection(int neighborIndex) {
   		int indexDifference = CURRENT_INDEX - neighborIndex;
   		
   		if (indexDifference == -7) {
   			return Direction.SOUTH;
   		}
   		else if (indexDifference == -1) {
   			return Direction.EAST;
   		}
   		else if (indexDifference == 1) {
   			return Direction.WEST;
   		}
   		else if (indexDifference == 7) {
   			return Direction.NORTH;
   		}
   		else {
   			return Direction.UNKNOWN;
   		}
   	}
   	
   	// Updates the direction of the Robot by given `amount`.
   	private static void addToDirection(int amount) {
   		ROBOT_DIR = (ROBOT_DIR+amount+4)%4;
   	}
   	
   	static void beep3Times() {
   		for (int i=0; i<3; i++) {
   			Sound.beep();
   		}
   	}

	// Moves the Robot forward (if distance > 0) or backward (if distance < 0) by `distance` centimeters.
   	static void travel(int distance) {
   		pilot.setLinearSpeed(20);
   		pilot.setLinearAcceleration(10);
   		pilot.travel(distance);
   	}
   	
   	// Moves the Robot forward until a cell with given `cellColor` is encountered.
   	static void moveForwardUntilColor(int cellColor) {
   		pilot.setLinearSpeed(5);
   		pilot.setLinearAcceleration(10);
   		pilot.forward();
   		while (getCellColor() != cellColor) {
   			Thread.yield();
   		}
   		pilot.stop();
   	}
   	
   	// Moves the Robot backward until it escapes from the cell with given `cellColor`.
	static void moveBackwardWhileColor(int cellColor) {
		pilot.setLinearSpeed(5);
		pilot.setLinearAcceleration(10);
		pilot.backward();
		while (getCellColor() == cellColor) {
			Thread.yield();
		}
		pilot.stop();
	}
    
   	// Lifts the lifting mechanism up as high as possible.
	static void liftLeverUp() {
		leverMotor.forward();
		while (!leverMotor.isStalled()) {
			Thread.yield();
		}
		leverMotor.flt();
	}
	
	// Lifts the lifting mechanism up as high as possible.
	static void liftLeverDown() {
		leverMotor.backward();
		while (!leverMotor.isStalled()) {
			Thread.yield();
		}
		leverMotor.flt();
	}
	
	// Grasps the ball located in the center of the green cell.
	// It is assumed that the Robot is at the beginning of the green cell when this method is called.
	// Returns the color of the ball.
	static int graspBall() {
		liftLeverDown();
		travel(8); // Places the lever under the ball
		int color = isBallRed() ? Colour.RED : Colour.BLUE;
		liftLeverUp();
		return color;
	}
	 
	// Releases the ball into the basket located in the center of the red or blue cell.
	// It is assumed that the robot is at the beginning of the red or blue cell when this method is called.
	static void releaseBall() {
		leverMotor.rotate(-90); // Drops the ball
		travel(-2); // Moves 2 cm backward in case the ball didn't drop
		liftLeverDown();
		liftLeverUp();
	}
	
	// Returns true if the color of the ball is red.
	// Else, returns false (which means blue).
	// It is assumed that a ball is present in front of the NXTLightSensor when this method is called.
	static boolean isBallRed() {
		nxtLightSampleProvider.fetchSample(nxtLightSamples, 0);
		System.out.println(nxtLightSamples[0]);
		return nxtLightSamples[0] >= 0.375;
	}
	
	// Writes given `text` to the middle of the LCD
	static void writeToLCD(String text) {
		graphicsLCD.clear();
		graphicsLCD.drawString(text, Robot.graphicsLCD.getWidth()/2, Robot.graphicsLCD.getHeight()/2 , GraphicsLCD.VCENTER|GraphicsLCD.HCENTER);
	}

}
