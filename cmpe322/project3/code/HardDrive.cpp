#include "HardDrive.h"
#include <fstream>

using namespace std;


/*
 * Deletes the head Process from 'readyQueue' of the system and inserts it to the wait queue of hard-drive.
 * If the newly added Process is the head of the wait queue, calls 'processWaitQueue' method.
 */
void HardDrive::readM_add(list<Process> *readyQueue, const int &simulationTime){

    Process currentProcess = readyQueue->front();
    readyQueue->pop_front();
    this->waitQueue.push_back(currentProcess);

    if( this->waitQueue.size() == 1 ){  // If 'currentProcess' is the only process in 'waitQueue', ...
        this->processWaitQueue(simulationTime);		// ... then it begins being processed immediately.
    }

    this->updateOutput(simulationTime);     // Prints updates of 'waitQueue' to "output_12.txt"

}


/*
 * Removes the head Process of the wait queue of the hard-drive and inserts it to 'readyQueue' of the system.
 * If there is another Process in the wait queue, calls 'processWaitQueue' method.
 */
void HardDrive::readM_remove(list<Process> *readyQueue , const int &simulationTime){

    Process doneProcess = this->waitQueue.front();
    this->waitQueue.pop_front();
    readyQueue->push_back(doneProcess);

    if( !this->waitQueue.empty() ){     // If there are other Processes in the wait queue of the hard-drive, ...
        this->processWaitQueue(simulationTime);		// ... head Process begins being processed.
    }

    this->updateOutput(simulationTime);     // Prints updates of 'waitQueue' to "output_12.txt"

}


/*
 * Processes the head Process of the wait queue of the hard-drive and
 * updates 'lastBlockNo' and 'completionTime' accordingly.
 */
void HardDrive::processWaitQueue(const int &simulationTime){

	Process &nextProcess = this->waitQueue.front();
        
    /*
     * 'nextProcess'.'lastInstNumber' was incremented by 1 after the "readM" operation is processed by the CPU.
     * Hence, we need to use 'lastInstNumber'-1  to access "readM" instruction of 'nextProcess' again 
     * to obtain the duration of the instruction and the number of block to be read.
     * Remember that the instructions of a Process are stored as a pair in the form  <name , duration>
     */
    this->completionTime = simulationTime + nextProcess.instructions[nextProcess.lastInstNumber-1].second;
    // substr(6) extracts the <blockNo> from a string in the from "readM_<blockNo>"
    this->lastBlockNo = stoi( (nextProcess.instructions[nextProcess.lastInstNumber-1].first).substr(6) );

}


/*
 * Creates/Updates a file named "output_12.txt" which prints processes in 'waitQueue' of the hard-drive at 'simulationTime'.
 * Every line of the file corresponds to a 'simulationTime.'
 */
void HardDrive::updateOutput(const int &simulationTime){
    ofstream outputHardDriveFile("output_12.txt", fstream::app);   // Opens "output_12.txt" file in append mode.
    outputHardDriveFile << simulationTime << "::HEAD-";
    for(Process p: this->waitQueue){
        outputHardDriveFile << p.name << "-";
    }
    outputHardDriveFile << "TAIL" << endl;
}
