#include "Printer.h"
#include <fstream>

using namespace std;


/*
 * Deletes the head Process from 'readyQueue' of the system and inserts it to the wait queue of the printer.
 * If the newly added Process is the head of the wait queue,
 * updates 'completionTime' using 'simulationTime' and the newly added Process.
 */
void Printer::dispM_add(list<Process> *readyQueue, const int &simulationTime){

    Process currentProcess = readyQueue->front();
    readyQueue->pop_front();
    this->waitQueue.push_back(currentProcess);

    if( this->waitQueue.size() == 1 ){  // If 'currentProcess' is the only process in 'waitQueue', ...
    	this->processWaitQueue(simulationTime);		// ... then it begins being processed immediately.
    }

    this->updateOutput(simulationTime);     // Prints updates of 'waitQueue' to "output_1<printerID>.txt"
}


/*
 * Removes the head Process of the wait queue of the printer and inserts it to 'readyQueue' of the system.
 * If there is another Process in the wait queue,
 * updates 'completionTime' using 'simulationTime' and the new head Process of the wait queue.
 */
void Printer::dispM_remove(list<Process> *readyQueue , const int &simulationTime){
   
    Process doneProcess = this->waitQueue.front();
    this->waitQueue.pop_front();
    readyQueue->push_back(doneProcess);

    if( !this->waitQueue.empty() ){     // If there are other Processes in the wait queue of the printer, ...
    	this->processWaitQueue(simulationTime);		// ... then head Process begins being processed.
    }

    this->updateOutput(simulationTime);
}


/*
 * Processes the head Process of the wait queue of the printer and
 * updates 'completionTime' accordingly.
 */
void Printer::processWaitQueue(const int &simulationTime){

    Process &nextProcess = this->waitQueue.front();
    
    /*
     * 'nextProcess'.'lastInstNumber' was incremented by 1 after the "dispM" operation is processed by the CPU.
     * Hence, we need to use 'lastInstNumber'-1  to access "dispM" instruction of 'currentProcess' again 
     * to obtain the duration of the operation.
     * Remember that instructions of a Process stored as a pair in the form  <name , duration>
     */
    this->completionTime = simulationTime + nextProcess.instructions[nextProcess.lastInstNumber-1].second;

}


/*
 * Creates/Updates a file named "output_1<printerNo>.txt" which prints processes in 'waitQueue' of the printer at 'simulationTime'.
 * Every line of the file corresponds to a 'simulationTime.'
 */
void Printer::updateOutput(const int &simulationTime){
    ofstream outputPrinterFile("output_1" + to_string(id) + ".txt" , fstream::app);   // Opens "output_1<printerNo>.txt" file in append mode.
    outputPrinterFile << simulationTime << "::HEAD-";
    for(Process p: this->waitQueue){
        outputPrinterFile << p.name << "-";
    }
    outputPrinterFile << "TAIL" << endl;
}
