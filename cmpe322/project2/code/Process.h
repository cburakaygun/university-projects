#ifndef PROCESS_H
#define PROCESS_H

#include <string>
#include <vector>

using namespace std;

/*
 * Representation of the Process block.
 */
struct Process{
    string name;            // Name of the process, e.g. "P3"
    string codeFileName;    // Name of the code file of the process, e.g. "1.code.txt"
    int arrivalTime;        // Arrival time of the process, e.g. 120

    /*
     * Note that instructions are stored in an array indices of which start from 0 and
     * lastInstNumber, which is the number of the last instruction that was executed, starts from 1.
     */
    vector<pair<string, int>> instructions;   // An array that stores the name & duration of each atomic instruction of the process.
    int lastInstNumber;     // Last instruction number that was executed. Instruction numbers start from 1.

    Process(string name , string codeFileName , int arrivalTime);

};

#endif