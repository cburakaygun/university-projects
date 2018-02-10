#ifndef PRINTER_H
#define PRINTER_H

#include "Process.h"
#include <deque>
#include <list>

/*
 * Representation of a printer.
 */
struct Printer{
    
    int id;     // ID of the printer. (printer no)
    int completionTime = 0;     // The moment (of simulation time) at which a "dispM" operation will be completed.
    deque<Process> waitQueue;   // Wait queue of the printer.
    

    /*
     * Deletes the head Process from 'readyQueue' of the system and inserts it to the wait queue of the printer.
     * If the newly added Process is the head of the wait queue,
     * updates 'completionTime' using 'simulationTime' and the newly added Process.
     */
    void dispM_add(list<Process> *readyQueue, const int &simulationTime);


    /*
     * Removes the head Process of the wait queue of the printer and inserts it to 'readyQueue' of the system.
     * If there is another Process in the wait queue,
     * updates 'completionTime' using 'simulationTime' and the new head Process of the wait queue.
     */
    void dispM_remove(list<Process> *readyQueue , const int &simulationTime);


    /*
     * Processes the head Process of the wait queue of the printer and
     * updates 'completionTime' accordingly.
     */
    void processWaitQueue(const int &simulationTime);
    

    /*
     * Creates/Updates a file named "output_1<printerNo>.txt" which prints processes in 'waitQueue' of the printer at 'simulationTime'.
     * Every line of the file corresponds to a 'simulationTime.'
     */
    void updateOutput(const int &simulationTime);

};

#endif
