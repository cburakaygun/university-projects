#ifndef SEMAPHORE_H
#define SEMAPHORE_H_H

#include "Process.h"
#include <deque>
#include <list>

/*
 * Represantation of a Semaphore. 
 */
struct Semaphore{
    int value;                  // The number of avalaible locks.
    deque<Process> waitQueue;   // Waiting queue for 'Process'es which wait for a lock.
};


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
bool waitS(Semaphore *S , list<Process> *readyQueue);


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
bool signS(Semaphore *S , list<Process> *readyQueue);


/*
 * Creates/Updates a file named "output_<semNumber>.txt" which prints processes in waitQueue of 'S' at 'simulationTime'.
 * Every line of the file corresponds to a 'simulationTime.'
 */
void updateOutputSem(Semaphore *S, const int &simulationTime , const int &semNumber);


#endif