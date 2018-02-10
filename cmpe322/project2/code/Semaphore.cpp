#include "Semaphore.h"
#include "Process.h"
#include <list>
#include <fstream>
#include <string>

using namespace std;


/*
 * A process must call this function with related Semaphore before executing its critical section.
 * 
 * If there is an available lock, the process gets the lock.
 * Else, the process in the front of 'readyQueue' is removed and added to waitQueue of 'S'.
 * (It is assumed that the process executing waitS_X is currently in the front of 'readyQueue'.)
 * 
 * If the process executing waitS_X gets an available lock, returns true.
 * Else, returns false.
 */
bool waitS(Semaphore *S , list<Process> *readyQueue){

    if( S->value > 0 ){    // If there is an available lock, ...
        S->value--;         // ... the process takes it.
        return true;
    
    }else{      // If there is no available lock, ...
        Process currentProcess = readyQueue->front();
        readyQueue->pop_front();    // ... the process executing waitS_X is removed from 'readyQueue' and ...
        S->waitQueue.push_back(currentProcess);     // ... is added into 'waitQueue' of 'S'.
        return false;
    }

}


/*
 * A process must call this function with related Semaphore after executing its critical section.
 * 
 * If there are any processes waiting for a lock in waitQueue of 'S', the process in the front of waitQueue
 * is removed and added into 'readyQueue'. (Logically, the process executing signS_X transfers the lock it holds to that process.)
 * Else, the lock which the process executing signS_X holds becomes available.
 * 
 * If a process is removed from waitQueue of 'S' and added to 'readyQueue', returns true.
 * Else, returns false.
 */
bool signS(Semaphore *S , list<Process> *readyQueue){

    if( S->waitQueue.empty() ){     // If there is no waiting process for a lock, ...
        S->value++;     // ... the lock which the process executing signS_X holds becomes available.
        return false;
    
    }else{      // If there is a process waiting for a lock, ...
        Process nextProcess = S->waitQueue.front();
        S->waitQueue.pop_front();       // ... removes that process from 'waitQueue' and ...
        readyQueue->push_back(nextProcess);     // ... adds it to 'readyQueue'.
        return true;
    }   

}


/*
 * Creates/Updates a file named "output_<semNumber>.txt" which prints processes in waitQueue of 'S' at 'simulationTime'.
 * Every line of the file corresponds to a 'simulationTime.'
 */
void updateOutputSem(Semaphore *S, const int &simulationTime , const int &semNumber){
    ofstream outputSemFile("output_" + to_string(semNumber) + ".txt" , fstream::app);   // Opens "output_<semNumber>.txt" file in append mode.
    outputSemFile << simulationTime << "::HEAD-";
    for(Process p: S->waitQueue){
        outputSemFile << p.name << "-";
    }
    outputSemFile << "TAIL" << endl;
}