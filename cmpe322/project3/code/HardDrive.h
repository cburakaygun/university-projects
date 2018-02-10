#ifndef HARDRIVE_H
#define HARDRIVE_H

#include "Process.h"
#include <deque>
#include <list>

/*
 * Represensation of a hard-drive.
 */
struct HardDrive{
    
    int lastBlockNo = -1;       // The last block number that is read from the hard-drive.
    int completionTime = 0;     // The moment (of simulation time) at which a "readM" operation will be completed.
    deque<Process> waitQueue;   // Wait queue of the hard-drive.
    
    
    /*
     * Deletes the head Process from 'readyQueue' of the system and inserts it to the wait queue of the hard-drive.
     * If the newly added Process is the head of the wait queue, calls 'processWaitQueue' method.
     */
    void readM_add(list<Process> *readyQueue, const int &simulationTime);


    /*
     * Removes the head Process of the wait queue of the hard-drive and inserts it to 'readyQueue' of the system.
     * If there is another Process in the wait queue, calls 'processWaitQueue' method.
     */
    void readM_remove(list<Process> *readyQueue , const int &simulationTime);
    
    
    /*
     * Processes the head Process of the wait queue of the hard-drive and
     * updates 'lastBlockNo' and 'completionTime' accordingly.
     */
    void processWaitQueue(const int &simulationTime);


    /*
     * Creates/Updates a file named "output_12.txt" which prints processes in 'waitQueue' of the hard-drive at 'simulationTime'.
     * Every line of the file corresponds to a 'simulationTime.'
     */
    void updateOutput(const int &simulationTime);

};

#endif
