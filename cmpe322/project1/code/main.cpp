#include "Process.h"
#include "DefinitionFileParser.h"
#include <iostream>
#include <string>
#include <vector>
#include <fstream>
#include <list>
#include <queue>

using namespace std;


const int QUANTUM = 100;    // Quantum time for Round Robin Scheduling

int simulationTime = 0;     // The time of the simulation, initially 0.
list<Process> readyQueue;   // A FIFO queue of 'Process'es


/*
 * Writes the names of the processes waiting in 'readyQueue' along with the current time
 * into a row of 'outputFile'.
 */
void updateOutput(ofstream *outputFile){
    *outputFile << simulationTime << "::HEAD-";
    for(Process p: readyQueue){
        *outputFile << p.name << "-";
    }
    *outputFile << "TAIL" << endl;
}


int main() {

    /*
     * All the processes defined in 'definition.txt' will be stored in this queue initially.
     * It is assumed that processes appear in increasing arrival time order in 'definition.txt' file.
     * Hence, this queue will store 'Process'es in increasing arrival time order.
     */
    queue<Process> processQueue;

    parseDefinitionFile(&processQueue);     // Parses 'definition.txt' and fills up 'processQueue'.

    ofstream outputFile("output.txt");  // Creates (opens) the output file.

    while(!processQueue.empty()){

        /*
         * Whenever the code reaches this point, 'readyQueue' will be empty.
         * This loop will increase the simulation time by the amount of 'QUANTUM' until a process arrives to the system.
         */
        while(processQueue.front().arrivalTime > simulationTime){
            updateOutput(&outputFile);
            simulationTime += QUANTUM;
        }

        /*
         * This loop inserts the processes that has arrived to the system (at or before the current simulation time) into
         * 'readyQueue'.
         */
        while(!processQueue.empty() && processQueue.front().arrivalTime <= simulationTime){
            Process p = processQueue.front();
            processQueue.pop();
            readyQueue.push_back(p);
        }

        updateOutput(&outputFile);

        /*
         * This while loop basically implements the Round Robin Scheduling.
         */
        while(!readyQueue.empty()){
            Process currentProcess = readyQueue.front();    // Gets the first (head) 'Process' of 'readyQueue'.
            readyQueue.pop_front();     // Removes the first (head) 'Process' from 'readyQueue'.

            int quantum = QUANTUM;      // Assigns a quantum time of 'QUANTUM' for 'currentProcess'.


            /*
             * Executes the atomic instructions of 'currentProcess' as long as there is still some quantum time to use and
             * the process has still some instructions to execute.
             * Also, updates 'lastInstNumber'. Remember that instruction numbers start from 1 and 'lastInstNumber' is
             * initially set to 0 when a 'Process' is created.
             * Also, remember that the indices of 'instructions' vector start from 0.
             */
            while( quantum > 0 && currentProcess.lastInstNumber != currentProcess.instructions.size() ){
                quantum -= currentProcess.instructions[currentProcess.lastInstNumber++];
            }

            simulationTime += QUANTUM-quantum;      // 'QUANTUM-quantum' gives the amount of time while loop above spends on executing 'currentProcess'.


            /*
             * While 'currentProcess' was being executed in while loop above, some other processes may have arrived to the system.
             * The while loop below inserts those processes into 'readyQueue'.
             */
            while(!processQueue.empty() && processQueue.front().arrivalTime <= simulationTime){
                Process p = processQueue.front();
                processQueue.pop();
                readyQueue.push_back(p);
            }


            /*
             * Remember that instruction numbers start from 1 and 'lastInstNumber' is initially set to 0 when a 'Process' is created.
             * Also, remember that the indices of 'instructions' vector start from 0.
             * Hence, when 'lastInstNumber' becomes equal to the size of 'instructions' vector,
             * it means that all the instructions of the process have been executed.
             * Otherwise, it means that the process has still some instructions to be executed and in this case,
             * we need to insert 'currentProcess' (which was removed from the queue before) into the (tail of) 'readyQueue' again.
             */
            if( currentProcess.lastInstNumber != currentProcess.instructions.size() ) {
                readyQueue.push_back(currentProcess);
            }

            updateOutput(&outputFile);
        }

    }

    outputFile.close();     // Closes the output file.

    return 0;

}