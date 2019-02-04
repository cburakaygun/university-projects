This is our hardware and software solution to the term project.

### Design of the Robot
The map (4×4 square grid) requires the robot to make plenty of 90 degrees point rotations throughout the tasks. We can rotate the robot by a certain amount with 2 different approaches: One of them is utilizing odometry and the other one is utilizing a **Gyro** sensor. On the preliminary, we thought that using a **Gyro** sensor was more reliable in terms of precision. However, as we observed the movements of the robot, we found out that the **Gyro** sensor is not so reliable. Hence, we decided to the calibrate and use odometry for 90 degree rotations. We still use the **Gyro** sensor, though. We rotate the robot using odometry and measure the value of the **Gyro** sensor. Then, we make additional rotations (again with odometry) if the **Gyro** value is far from the desired amount. As a result, 1 of our 4 sensors is still a **EV3 Gyro Sensor**.

The map separates some cells with walls. This requires our robot to detect obstacles. We can use either a Touch sensor or an **Ultrasonic** sensor to detect an obstacle. We prefer to use an **Ultrasonic** sensor as it provides us with the ability of detecting an obstacle without crashing into it (touching it). On the preliminary, we designed the Ultrasonic sensor as a fixed component. In the final, we think it will be better if the **Ultrasonic** sensor has the ability to change its direction independent of the robot itself since this feature will speed up the process of cell (wall) examination and the rotation of the robot is not so fast. So, we mounted a **EV3 UltrasonicSensor** to a **NXTRegulatedMotor**.

The cells of the map have different colors and those colors are important for us because they provide us with some information we need to complete the tasks. As a result, our robot is required to detect the color of the cells. In addition to the cell colors, our robot is required to detect the color of the balls in order to move them to the correct baskets. For detecting the possible cell colors (red, green, blue, white and black), we need to utilize **EV3 Color Sensor** because of its ability to measure the color (RGB) of an object. **NXT Light Sensor** does not have the ability to detect the color, but it can detect the intensity of the red light reflected from an object. Since the balls are either red or blue, we can use **NXT Light Sensor** to detect the reflected red light from a ball to detect its color.

Lastly, our robot is required to have the ability to grasp a ball and release (drop) it into a basket. We use one **EV3 Medium Servo Motor** along with some gear mechanism to capture this behaviour.

Overall, we construct a 2-wheeled (with 1 steel ball) robot with 4 sensors (1 **EV3 Ultrasonic Sensor**, 1 **EV3 Gyro Sensor**, 1 **EV3 Color Sensor** and 1 **NXT Light Sensor**) and 4 motors (2 **EV3 Large Servo Motors**, 1 **NXT Motor** and 1 **EV3 Medium Servo Motor**) as follows:

*  **NXT Motor** is mounted on the top side of the robot.
*  **EV3 Ultrasonic Sensor** is mounted to the **NXT Motor** facing towards the forward diretion of the robot.
*  **EV3 Gyro Sensor** is mounted on the top side of the robot in such a way that it stays in parallel to the floor.
*  **EV3 Color Sensor** is mounted in such a way that it faces towards the floor.
*  **NXT Light Sensor** is mounted on the front side of the robot facing towards the forward direction of the robot.
*  **EV3 Medium Servo Motor** is mounted so that it utilizes the lifting mechanism which is mounted on the front side of the robot.


### Tasks
#### Map Making
Although the actual map is a 4×4 grid, we keep a 7×7 grid as our map. The algorithm assumes that the robot is initially in the middle cell of the 7×7 grid and its direction is the north. This way, the actual map (4×4 grid) is matched with a sub 4×4 grid of the 7×7 grid. The robot examines the cell it currently stands on using multiple sensors and moves to the next cell in **Depth First Search** manner.

Our robot has the ability to detect the walls via **EV3 Ultrasonic Sensor** and the color of the cells via **EV3 Color Sensor**. It also has the ability to rotate itself and the **Ultrasonic** sensor 90 degrees . When the mapping algorithm starts, the robot detects the color of the cell and examines the 4 sides of the cell to check if there are any walls. Then, it moves to the next cell to examine it. The robot moves from cell to cell as in **Depth First Search** until all the possible cells are discovered.

We represents a cell with a **Cell class** which stores some information about its color, walls and neighbours. While the robot moves between cells, the algorithm creates some links between those to maintain the positions of the cells with respect to each other.

#### Localization
In one of the labs of the course, we had examined the **Monte Carlo Localization** algorithm on one dimension where the robot moves on a straight line and preditcs its position according to its distance to obstacles. For this project, we developed a similar algorithm where we assign 4 particles to each white cells (since the robot can be on only a white cell initially). Each particle of a cell has a distinct direction (north, south, east and west). A particle represents the robot's possible cell (position), and direction in that cell. The robot moves along the map and eliminates the particles according to the information it gets from its environment (cell color, cell walls, etc.) until only 1 particle is left.

For example, the particles which currently do not on a cell with the same color as the cell the Robot curently stands on are eliminated; or, if there is a wall one any of the 4 sides of the Robot, the particles which curently do not have a wall on those sides are eliminated; or, if the Robot has a cell with color C on side S, the particles which currently do not have a cell with color C on their side S are eliminated.

#### Path Planning
Our mapping algorithm maintains an array of Cells (of size 49) and each Cell has its four neighbor indices (or **WALL**, or **UNKNOWN**). This array serves as a graph since each Cell acts as a node and each index number of a Cell's neighbor acts as a vertex. For path planning, we slightly modified the algorithm stated [here](https://www.geeksforgeeks.org/shortest-path-unweighted-graph/) which is a shortest path algorithm built on **Breadth First Search**. Our algorithm can find a path between 2 given Cells (indices) while avoiding some specified Cells on that path.

#### Grasping/Releasing the Ball
Our robot has a **EV3 Medium Servo Motor** for grasping/releasing ball ability. We constructed a lifting mechanism in front of our robot and connected it to the **EV3 Medium Servo Motor** via 2 gears. When the **EV3 Medium Servo Motor** runs forward, the lever is lifted up and when the **EV3 Medium Servo Motor** runs backward, the lever is lifted down.

#### Software
We used **Subsumption** architecture on **LeJOS** to implement the **Button Interface**. There are **5** behaviours with the following run conditions (Listed from the highest priority to the lowest priority):

*  **IDLE**: Takes control when **ENTER** button is pressed.
*  **RESET*: Takes control when **ESCAPE** button is pressed.
*  **MAPPING**: Takes control when **UP** button is pressed or the mapping process should be reset.
*  **LOCALIZATION**: Takes control when **DOWN** button is pressed or the localization process should be reset.
*  **BASKETING**: Takes control when **LOCALIZATION** is done.

Our overall code design are as follows:

Classes that run only on the Robot (EV3Brick):

*  Robot: A static class which represents the Robot. This class initializes the robot and its sensors and motors. Also, it provides methods to control the Robot and its sensors and motors.
*  Mapping: A **Behaviour** object which is responsible for making the Robot examine its environment in order to create its map. This object also sends mapping data to the PC.
*  Localization: A **Behaviour** object which is responsible for making the Robot examine its environment in order to localize itself. This object also sends localization data to the PC.
*  PathPlanning: A helper static class which provides a method for finding the shortest path between given 2 Cells while avoiding some güven Cells on that path.
*  Basketing: A **Behaviour** object which is responsible for moving the Robot to the green cell, grasping the ball on it, basketing the ball to the coresspoding cells (red or blue) for each of the red and blue balls. This object also sends basketing data to the PC.
*  TermProject: Main class running on the Robot. This class is responsible for storing some common constants and maintaining a socket connection to the PC. This class also contains the definitions of **IdleBehavior** and the **ResetBehaviour** objects.

Classes that run both on the Robot (EV3Brick) and the PC:

*  Cell: A class which represents a grid cell of the map. This class also stores a Cell array of size 49 (7×7 grid) which represents the map.
*  Colour: A static helper class which represents some colors (e.g. red, blue, etc.) as integers. This class is used for stating the cell and the ball colors.
*  Direction: A static helper class which represents the main directions (north, east, south, west) as integers. This class is used for stating the direction of the Robot and the localization particles.

Classes that run only on the PC:

*  PCMain: Main class that runs on the PC. This class is reponsible for maintaining a socket connection with the Robot and updating the map on the PC screen according to the current operation of the Robot.
*  PCMonitor: A **JFrame** object which handles the graphics of the map on the PC screen.

