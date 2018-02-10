#include "Process.h"
#include "DefinitionFileParser.h"
#include "Semaphore.h"
#include "Printer.h"
#include "HardDrive.h"
#include "Cache.h"
#include <iostream>
#include <string>
#include <vector>
#include <fstream>
#include <list>
#include <queue>

using namespace std;

const int QUANTUM = 100;    // Quantum time for Round Robin Scheduling
const int TOTAL_PRINTER_NUMBER = 2;     // Number of total printers in the system.
const int CACHE_SIZE = 2;       // Cache size in blocks.

int simulationTime = 0;     // The time of the simulation, initially 0.
list<Process> readyQueue;   // A FIFO queue of 'Process'es, ready queue of the system.
ofstream outputFile;        // "output.txt" file.


/*
 * Writes the names of the processes waiting in 'readyQueue' along with the current time
 * into a row of 'outputFile'.
 */
void updateOutput(){
    outputFile << simulationTime << "::HEAD-";
    for(Process p: readyQueue){
        outputFile << p.name << "-";
    }
    outputFile << "TAIL" << endl;
}


int main() {

    Semaphore semArray[10];     // There are 10 semaphores in the system. Each semaphore corresponds to a critical resource.
    for(int i=0 ; i<10 ; i++){
        semArray[i].value = 1;  // Since only one process at a time is allowed to use a critical resource, available locks are initialized to 1.
    }

    Printer printerArray[TOTAL_PRINTER_NUMBER];     // The printers of the system.
    for(int i=0 ; i<TOTAL_PRINTER_NUMBER ; i++){
        printerArray[i].id = i;     // Initializes the IDs of the printers. (printer no)
    }

    HardDrive hardDrive;        // The hard-drive of the system.

    Cache hardDriveCache;       // The cache for the hard-drive.
    hardDriveCache.cacheSize = CACHE_SIZE;

    /*
     * All the processes defined in 'definition.txt' will be stored in this queue initially.
     * It is assumed that processes appear in increasing arrival time order in 'definition.txt' file.
     * Hence, this queue will store 'Process'es in increasing arrival time order.
     */
    queue<Process> processQueue;

    parseDefinitionFile(&processQueue);     // Parses 'definition.txt' and fills up 'processQueue'.

    outputFile.open("output.txt");  // Opens "output.txt" file.

    while(!processQueue.empty()){

        /*
         * Whenever the program reaches this point, 'readyQueue' will be empty.
         * This loop will increase the simulation time by the amount of 'QUANTUM' until a process arrives to the system.
         */
        while(processQueue.front().arrivalTime > simulationTime){
            updateOutput();
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

        updateOutput();

        /*
         * This while loop basically implements the Round Robin Scheduling.
         */
        while( !readyQueue.empty() ){
            Process *currentProcess = &readyQueue.front();    // Gets the first (head) 'Process' of 'readyQueue'.

            int quantum = QUANTUM;      // Assigns a quantum time of 'QUANTUM' for 'currentProcess'.

            /*
             * Executes the atomic instructions of 'currentProcess' as long as there is still some quantum time to use (quantum > 0) and
             * the process has still some instructions to execute (lastInsNumber != instructions.size) and
             * the process was not blocked in a waitS() call and
             * the process didn't go into an I/O wait queue.
             * Also, updates 'lastInstNumber'. Remember that instruction numbers start from 1 and 'lastInstNumber' is
             * initially set to 0 when a 'Process' is created.
             * Also, remember that the indices of 'instructions' vector start from 0.
             */
            while( quantum > 0 && currentProcess->lastInstNumber != currentProcess->instructions.size() ){
                string currentInstName = currentProcess->instructions[currentProcess->lastInstNumber].first;
                int currentInstDuration = currentProcess->instructions[currentProcess->lastInstNumber].second;                
                currentProcess->lastInstNumber++;
                
                if( currentInstName.compare(0,5,"dispM") == 0 ){    // If current instruction is in the form "dispM_<printerNo>", ...
                    int printerNo = stoi(currentInstName.substr(6));    // ... gets <printerNo> from "dispM_<printerNo>" and ...
                    printerArray[printerNo].dispM_add(&readyQueue , simulationTime);    // ... sends 'currentProcess' to the wait queue of the printer <printerNo>
                    break;      // Stops executing 'currentProcess' to start executing the next process in 'readyQueue'.


                }else if( currentInstName.compare(0,5,"readM") == 0 ){  // If current instruction is in the form "readM_<blockNo>", ...
                    int blockNo = stoi(currentInstName.substr(6));      // ... gets <blockNo> from "readM_<blockNo>".
                    if( !hardDriveCache.contains(blockNo) ){        // If the cache does not contain <blockNo>, ...
                        hardDrive.readM_add(&readyQueue , simulationTime);  // ... sends 'currentProcess' to the wait queue of the hard-drive and ...
                        break;      // ... stops executing 'currentProcess' to start executing the next process in 'readyQueue'.
                    }

                }else if( currentInstName.compare(0,5,"waitS") == 0 ){    // If current instruction is in the form "waitS_<Digit>", ...
                    int semNumber = stoi(currentInstName.substr(6));  // ... gets <Digit> from "waitS_<Digit>".
                    
                    if( !waitS( &semArray[semNumber] , &readyQueue) ){      // Executes waitS_<Digit> instruction.
                        /*
                         * If the program reaches here, it means that waitS call returned false.
                         * That means the process couldn't get an available lock and removed from 'readyQueue' and added into related 'waitQueue'.
                         */
                        updateOutputSem(&semArray[semNumber] , simulationTime , semNumber);     // Prints updates of 'waitQueue' to "output_<semNumber>.txt"
                        break;      // Stops executing 'currentProcess' to start executing the next process in 'readyQueue'.
                    }

                }else if( currentInstName.compare(0,5,"signS") == 0 ){      // If current instruction is in the form "signS_<Digit>", ...
                    int semNumber = stoi(currentInstName.substr(6));      // ... gets <Digit> from "waitS_<Digit>".
                    
                    if( signS( &semArray[semNumber] , &readyQueue ) ){      // Executes signS_<Digit> instruction.
                        /*
                         * If the program reaches here, it means that signS call returned true.
                         * That means a process is removed from 'waitQueue' and added into 'readyQueue'.
                         */
                        updateOutputSem(&semArray[semNumber] , simulationTime , semNumber);     // Prints updates of 'waitQueue' to "output_<semNumber>.txt"
                        updateOutput();     // Prints updates of 'readyQueue' to "output.txt"
                    }
                
                }else{      // If the current instruction is a "normal" instruction, ...
                    quantum -= currentInstDuration;     // ... executes it.
                    simulationTime += currentInstDuration;

                    /*
                     * While a "normal" instruction of 'currentProcess' was being executed, some IO operations may have been completed.
                     */
                    
                    // Checks whether an IO operation is completed at the printers.
                    for(int i=0 ; i<TOTAL_PRINTER_NUMBER ; i++){
                        
                        Printer *printer = &printerArray[i];
                        
                        /*
                         * If an "dispM" operation was completed by the current 'simulationTime',
                         * removes that Process from the wait queue of 'printer' and inserts it to the tail of 'readyQueue'.
                         */
                        if( !printer->waitQueue.empty() && printer->completionTime <= simulationTime ){
                            printer->dispM_remove(&readyQueue , simulationTime);
                        }
                    
                    }


                    /*
                     * Checks whether an IO operation is completed at the hard-drive.
                     * If an "readM" operation was completed by the current 'simulationTime',
                     * removes that Process from the wait queue of the hard-drive and inserts it to the tail of 'readyQueue'.
                     * Also, inserts the block no that was read from the hard-drive during the last "readM" operation into the cache.
                     */
                    if( !hardDrive.waitQueue.empty() && hardDrive.completionTime <= simulationTime ){                        
                        hardDriveCache.insert( hardDrive.lastBlockNo );     // 'lastBlockNo' contains the block no 
                                                                            // that was read from the hard-drive during the last "readM" operation.
                        hardDrive.readM_remove(&readyQueue , simulationTime);
                    }


                    /*
                    * While a "normal" instruction of 'currentProcess' was being executed, some other processes may have arrived to the system.
                    * The while loop below inserts those processes into 'readyQueue'.
                    */
                    while(!processQueue.empty() && processQueue.front().arrivalTime <= simulationTime){
                        Process p = processQueue.front();
                        processQueue.pop();
                        readyQueue.push_back(p);
                    }

                }
            }


            /*
             * Remember that instruction numbers start from 1 and 'lastInstNumber' is initially set to 0 when a 'Process' is created.
             * Also, remember that the indices of 'instructions' vector start from 0.
             * Hence, when 'lastInstNumber' becomes equal to the size of 'instructions' vector,
             * it means that all the instructions of the process have been executed.
             */
            if( currentProcess->lastInstNumber == currentProcess->instructions.size() ){
                readyQueue.pop_front();     // 'currentProcess' has been completed. It is removed from 'readyQueue' (and also from the system).
            }

            /*
             * If 'currentProcess' gets blocked (removed from 'readyQueue' and added into a 'waitQueue') while executing a 'waitS' or 'readM' or 'dispM',
             * then it is known that it still has some instructions to execute (i.e. lastInstNumber != instructions.size) (because the last instruction is always 'exit')
             * AND its quantum is not used up. (i.e. currently, quantum > 0) (because those 3 instructions consume 0 time from quantum).
             * 
             * If the program skips the IF statement above, then there are 2 possibilities regarding 'currentProcess' at this moment:
             *      1) 'currentProcess' has been blocked because of a call to 'waitS' or 'readM' or 'dispM' (and it has been removed from 'readyQueue').
             *      2) 'currentProcess' is still in the front of 'readyQueue' and just ran out of quantum time (quantum <= 0).
             */
            
            else if( quantum <= 0 ){   // If this condition is true, then 2nd possibility mentioned above has occured. In this case, ...
                Process p = *currentProcess;
                readyQueue.pop_front();     // ... 'currentProcess' is moved from the front of 'readyQueue' to the back.
                readyQueue.push_back(p);
                
            }

            /*
             * If the program skips both IF and ELSE-IF statements above, then 1st possibility mentioned above has occured. ('currentProcess' has been blocked.)
             * Since 'currentProcess' is removed from 'readyQueue' while blocked, there is nothing more to do here.
             */

            updateOutput();     // Prints updates of 'readyQueue' to "output.txt"
        }

    }

    outputFile.close();     // Closes "output.txt" file.

    return 0;

}
