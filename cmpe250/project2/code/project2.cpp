/*
Student Name:       Cemal Burak Aygün
Student Number:     2014400072
Project Number:     2
Operating System:   Xubuntu (Ubuntu 14.04) (on VirtualBox)
Compile Status:     Compiles correctly.
Program Status:     Gives output correctly.
Notes:

*/

#include <iostream>
#include <queue>
#include <vector>
#include <iomanip>

using namespace std;


/*
 * Representation of CPU and output device.
 */
struct Device{
    double power;       // 'power' means "frequency" for a CPU , "quantum" for an output device.
    bool isIdle = true; // At the beginning, every device is idle.
    double activeTime = 0;  // Total time this Device spends working on Tasks.

    Device( double power ){
        this->power = power;
    }
};


/*
 * Representation of task.
 */
struct Task{
    double arrivalTime;
    double cpuWork;
    double outputWork;
    int index;              // The index of the Task in 'TASK' (vector).
    int deviceIndex = -1;   // The index of the Device in 'CPU' or 'OUTPUT' (vectors) to which this Task is assigned.
    double waitTime = 0;    // The time that passed in 'cpuQueue' and 'outputQueue' as total.
    double totalTime = 0;   // The time that passed in the system. (Exit time - 'arrivalTime')

    Task( double arrivalTime , double cpuWork , double outputWork , int index , int deviceIndex ){
        this->arrivalTime = arrivalTime;
        this->cpuWork = cpuWork;
        this->outputWork = outputWork;
        this->index = index;
        this->deviceIndex = deviceIndex;
    }

};


/*
 * Representation of event.
 * An Event is a part of a Task.
 * For a Task, arriving at a CPU is an Event, departing from a CPU is another Event, etc.
 */
struct Event{
    char type;   // '1': arriving at a CPU        '2': departing from a CPU = arriving at an output device
                 // '3': requeueing in 'outputQueue'     '4': departing from an output device
    double time;    // The moment when this event occurs.
    int taskIndex;  // The index of the Task in 'TASK' (vector) to which this Event is related.

    Event( char type , double time , int taskIndex ){
        this->type = type;
        this->time = time;
        this->taskIndex = taskIndex;
    }
};


vector<Device> CPU , OUTPUT;
vector<Task> TASK;


/*
 * The comparator for 'cpuQueue'
 * in which the Task pointers are sorted in non-decreasing order
 * according to their corresponding Task's 'cpuWork'.
 *
 * If the 'cpuWork's are equal to each other,
 * the Task pointer that is added to the queue earlier comes first.
 */
struct compCPUWork {
    bool operator()( Task *t1 , Task *t2 ){
        return (*t1).cpuWork > (*t2).cpuWork;
    }
};


/*
 * The comparator for 'events' (queue)
 * in which the Events are sorted in non-decreasing order according to their 'time'.
 *
 * If the 'time's are equal to each other;
 *      if the 'type's of the Events are equal to each other,
 *      the Event whose corresponding Task has smaller 'deviceID' comes first
 *
 *      if the 'type's of the Events are not equal to each other,
 *      the Event that is closer to the exit of the system comes first.
 *      (the bigger the Event 'type' is, the closer to the exit the Event is)
 */
struct compTime {
    bool operator()( const Event &e1 , const Event &e2 ){
        if( e1.time == e2.time ){

            if( e1.type == e2.type ){
                return TASK[e1.taskIndex].deviceIndex > TASK[e2.taskIndex].deviceIndex;
            }else{
                return e1.type < e2.type;
            }

        }else{
            return e1.time > e2.time;
        }
    }
};


/*
 * The queue in which the Tasks arriving at CPUs wait when all the CPUs are busy.
 */
priority_queue< Task* , vector<Task*> , compCPUWork > cpuQueue;
double maxCPUQueueSize = 0;     // The maximum current size of 'cpuQueue'.


/*
 * The queue in which the Tasks arriving at output devices wait when all the output devices are busy.
 */
queue<Task*> outputQueue;
double maxOUTPUTQueueSize = 0;  // The maximum current size of 'outputQueue'.


/*
 * The queue that stores Events that are going to occur.
 */
priority_queue< Event , vector<Event> , compTime > events;


/*
 * Returns:
 *      the index of the first idle Device in vec.
 *      -1 if there is no idle Device in vec at the moment.
 *
 * vec: 'CPU' (vector) or 'OUTPUT' (vector)
*/
int firstIdleIndex( vector<Device> &vec ){
    for( int i = 0 ; i < vec.size() ; i++ ){
        if( vec[i].isIdle ){
            return i;
        }
    }
    return -1;
}


/*
 * Handles the Tasks that visits the CPU part.
 * If there are any idle CPUs, it assigns the Task to the one with smaller ID,
 * else it sends the Task to the 'cpuQueue'.
 *
 * currentTask:     The task that arrives at the CPU part.
 * currentTime:     The moment when 'currentTask' arrives at the CPU part.
 * firstIdleCPU:    The index of the first idle CPU in 'CPU' (vector). -1 if unknown.
 */
void arrivalCPU(Task &currentTask, double &currentTime, int firstIdleCPU){

    if( firstIdleCPU == -1 ){
        firstIdleCPU = firstIdleIndex( CPU );
    }

    if( firstIdleCPU != -1 ) {      // If there is an idle CPU...

        currentTask.deviceIndex = firstIdleCPU;

        Device &currentCPU = CPU[firstIdleCPU];
        currentCPU.isIdle = false;
        double serviceTime = currentTask.cpuWork / currentCPU.power;
        currentCPU.activeTime += serviceTime;


        //cout << "T:" << currentTime << endl;
        //cout << "\tprocess#" << currentTask.index+1 << " goes to CPU#" << firstIdleCPU+1 << " after waiting " << currentTask.waitTime << " seconds" << endl;
        //cout << "\tCPU departure event set at T=" << currentTime+serviceTime << endl << endl;

        events.push( Event('2' , currentTime+serviceTime , currentTask.index) );   // Creates a "departure from CPU" event.

    }else{      // If there is no CPU that is idle at the moment...

        //cout << "T:" << currentTime << endl;
        //cout << "\tprocess#" << currentTask.index+1 << " goes to first level queue" << endl << endl;

        currentTask.waitTime = currentTime;

        cpuQueue.push( &currentTask );
        if( cpuQueue.size() > maxCPUQueueSize ){
            maxCPUQueueSize = cpuQueue.size();
        }

    }

}


/*
 * Handles the Tasks that visits the output device part.
 * If there are any idle output devices, it assigns the Task to the one with smaller ID,
 * else it sends the Task to the 'outputQueue'.
 *
 * currentTask:     The task that arrives at the output device part.
 * currentTime:     The moment when 'currentTask' arrives at the output device part.
 * firstIdleCPU:    The index of the first idle output device in 'OUTPUT' (vector). -1 if unknown.
 */
void arrivalOUTPUT(Task &currentTask, double &currentTime, int firstIdleOUTPUT){

    if( firstIdleOUTPUT == -1 ){
        firstIdleOUTPUT = firstIdleIndex( OUTPUT );
    }

    if( firstIdleOUTPUT != -1 ) {   // If there is an idle output device...

        currentTask.deviceIndex = firstIdleOUTPUT;

        Device &currentOUTPUT = OUTPUT[firstIdleOUTPUT];
        currentOUTPUT.isIdle = false;
        double serviceTime;
        if( currentTask.outputWork <= currentOUTPUT.power ){
            serviceTime = currentTask.outputWork;
            events.push( Event('4' , currentTime+serviceTime , currentTask.index) );  // Creates a "departure from output device" event.
        }else{
            serviceTime = currentOUTPUT.power;
            currentTask.outputWork = currentTask.outputWork - currentOUTPUT.power;
            events.push( Event('3' , currentTime+serviceTime , currentTask.index) );  // Creates a "requeue in 'outputQueue'" event.
        }
        currentOUTPUT.activeTime += serviceTime;


        //cout << "T:" << currentTime << endl;
        //cout << "\tprocess#" << currentTask.index+1 << " goes to IO_Unit#" << firstIdleOUTPUT+1 << " after waiting " << currentTask.waitTime << " seconds" << endl;
        //cout << "\tIO Unit departure event set at T=" << currentTime+serviceTime << endl << endl;


    }else{      // If there is no output device that is idle at the moment...

        //cout << "T:" << currentTime << endl;
        //cout << "\tprocess#" << currentTask.index+1 << " goes to second level queue" << endl << endl;

        currentTask.waitTime = currentTime - currentTask.waitTime;

        outputQueue.push ( &currentTask );
        if( outputQueue.size() > maxOUTPUTQueueSize ){
            maxOUTPUTQueueSize = outputQueue.size();
        }

    }

}


/*
 * Handles the Tasks that are done at the CPU part.
 *
 * currentTask:     The task that is done at the CPU part.
 * currentTime:     The moment when 'currentTask' is done at the CPU part.
 */
void departureCPU(Task &currentTask, double &currentTime){

    //cout << "T:" << currentTime << endl;
    //cout << "\tprocess#" << currentTask.index+1 << " is done at CPU#" << currentTask.deviceIndex+1 << endl << endl;

    if( !cpuQueue.empty() ){    // If there is a Task that is waiting for the CPU part...

        Task *nextCPUTask = cpuQueue.top();
        cpuQueue.pop();
        (*nextCPUTask).waitTime = currentTime - (*nextCPUTask).waitTime;

        arrivalCPU(*nextCPUTask, currentTime, currentTask.deviceIndex);

    }else{
        CPU[currentTask.deviceIndex].isIdle = true;
    }

    arrivalOUTPUT(currentTask, currentTime, -1);

}


/*
 * Handles the Tasks that are done at the output device part.
 *
 * currentTask:     The task that is done at the output device part.
 * currentTime:     The moment when 'currentTask' is done at the output device part.
 */
void departureOUTPUT(Task &currentTask, double &currentTime){

    //cout << "T:" << currentTime << endl;
    //cout << "\tprocess#" << currentTask.index+1 << " is done at IO_Unit#" << currentTask.deviceIndex+1 << endl << endl;

    currentTask.totalTime = currentTime - currentTask.arrivalTime;

    if( !outputQueue.empty() ){     // If there is a Task that is waiting for the OUTPUT device part...

        Task *nextOUTPUTTask = outputQueue.front();
        outputQueue.pop();
        (*nextOUTPUTTask).waitTime = currentTime - (*nextOUTPUTTask).waitTime;

        arrivalOUTPUT(*nextOUTPUTTask, currentTime, currentTask.deviceIndex);

    }else{      // If there is no Task that is waiting for the OUTPUT device part...
        OUTPUT[ currentTask.deviceIndex ].isIdle = true;
    }

}


/*
 * Handles the Tasks that are requeued in 'outputQueue'.
 *
 * currentTask:     The task that is requeued in 'outputQueue'.
 * currentTime:     The moment when 'currentTask' is requeued in 'outputQueue'.
 */
void reQueueOUTPUT(Task &currentTask, double &currentTime){

    //cout << "T:" << currentTime << endl;
    //cout << "\tprocess#" << currentTask.index+1 << " is done at IO_Unit#" << currentTask.deviceIndex+1 << endl << endl;

    if( outputQueue.empty() ){      // If there is no Task that is waiting for output device part...

        if( currentTask.outputWork > OUTPUT[currentTask.deviceIndex].power ){

            currentTask.outputWork = currentTask.outputWork - OUTPUT[currentTask.deviceIndex].power;
            events.push( Event('3', currentTime+OUTPUT[currentTask.deviceIndex].power , currentTask.index) );   // Creates a "requeue in 'outputQueue'" event.

        }else{
            events.push( Event('4', currentTime+currentTask.outputWork , currentTask.index) );  // Creates a "departure from OUTPUT device" event.
        }

    }else{      // If there is a Task that is waiting for the OUTPUT device part...

        //cout << "T:" << currentTime << endl;
        //cout << "\tprocess#" << currentTask.index+1 << " goes to second level queue" << endl << endl;

        Task *nextOUTPUTTask = outputQueue.front();
        outputQueue.pop();
        (*nextOUTPUTTask).waitTime = currentTime - (*nextOUTPUTTask).waitTime;

        arrivalOUTPUT(*nextOUTPUTTask, currentTime, currentTask.deviceIndex);

        currentTask.waitTime = currentTime - currentTask.waitTime;
        outputQueue.push( &currentTask );
        if( outputQueue.size() > maxOUTPUTQueueSize ){
            maxOUTPUTQueueSize = outputQueue.size();
        }

    }

}


/*
 * Returns the index of Device that has the biggest 'activeTime' in 'vec'.
 *
 * Precondition:    The size of 'vec' is at least 1.
 */
int greatestActiveTime( vector<Device> &vec ){
    int index = 0;
    for( int i = 1 ; i < vec.size() ; i++ ){
        if( vec[i].activeTime > vec[index].activeTime ){
            index = i;
        }
    }
    return index;
}


int main( int argc, char* argv[] ){

    if (argc != 3) {
        cout << "Run the code with the following command: ./project2 [input_file] [output_file]" << endl;
        return 1;
    }

    freopen( argv[1] , "r" , stdin );

    int numCPUs;        // Total number of CPUs.
    cin >> numCPUs;
    for( int i = 0 ; i < numCPUs ; i++ ){
        double frequency;
        cin >> frequency;
        CPU.push_back( Device(frequency) );
    }

    int numOUTPUTs;     // Total number of OUTPUT devices.
    cin >> numOUTPUTs;
    for( int i = 0 ; i < numOUTPUTs ; i++ ){
        double quantum;
        cin >> quantum;
        OUTPUT.push_back( Device(quantum) );
    }

    int numTASKs;       // Total number of tasks.
    cin >> numTASKs;
    for( int i = 0 ; i < numTASKs ; i++ ){
        double arrivalTime;
        cin >> arrivalTime;
        double cpuWork;
        cin >> cpuWork;
        double outputWork;
        cin >> outputWork;
        TASK.push_back( Task(arrivalTime , cpuWork , outputWork , i , i-numTASKs ) );
    }

    for( int i = 0 ; i < numTASKs ; i++ ){
        events.push( Event( '1' , TASK[i].arrivalTime , i ) );    // Creates an "arrive at CPU" event.
    }

    double currentTime = 0;         // The current moment of the simulation.
    while( !events.empty() ){

        Event currentEvent = events.top();
        events.pop();
        currentTime = currentEvent.time;
        char currentEventType = currentEvent.type;

        if( currentEventType == '1' ){
            arrivalCPU(TASK[currentEvent.taskIndex], currentTime, -1);
        }else if( currentEventType == '2' ){
            departureCPU(TASK[currentEvent.taskIndex], currentTime);
        }else if( currentEventType == '3' ){
            reQueueOUTPUT(TASK[currentEvent.taskIndex], currentTime);
        }else{  // if currentEventType == '4'
            departureOUTPUT(TASK[currentEvent.taskIndex], currentTime);
        }

    }

    freopen( argv[2] , "w" , stdout );

    cout << maxCPUQueueSize << endl;
    cout << maxOUTPUTQueueSize << endl;

    cout << greatestActiveTime( CPU ) + 1 << endl;
    cout << greatestActiveTime( OUTPUT ) + 1 << endl;

    double totalWaitTime = 0;       // Total 'waitTime' of all Tasks.
    for( int i = 0 ; i < numTASKs ; i++ ){
        totalWaitTime += TASK[i].waitTime;
    }
    cout << setprecision(6) << fixed << totalWaitTime / numTASKs << endl;

    double longestWaitTime = TASK[0].waitTime;      // The biggest 'waitTime' of all Tasks.
    for( int i = 1 ; i < numTASKs ; i++ ){
        if( TASK[i].waitTime > longestWaitTime ){
            longestWaitTime = TASK[i].waitTime;
        }
    }
    cout << setprecision(6) << fixed << longestWaitTime << endl;

    double totalTotalTime = 0;      // Total 'totalTime' of all Tasks.
    for( int i = 0 ; i < numTASKs ; i++ ){
        totalTotalTime += TASK[i].totalTime;
    }
    cout << setprecision(6) << fixed << totalTotalTime / numTASKs;

    return 0;

}