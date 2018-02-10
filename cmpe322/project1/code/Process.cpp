#include "Process.h"
#include <fstream>

using namespace std;

Process::Process(string name , string codeFileName , int arrivalTime){

    this->name = name;                  // Name of the process, e.g. "P3"
    this->codeFileName = codeFileName;  // Name of the code file of the process, e.g. "1.code.txt"
    this->arrivalTime = arrivalTime;    // Arrival time of the process, e.g. 120

    this->lastInstNumber = 0;   // Last instruction number that was executed. Instruction numbers start from 1.
                                // Initially, no instructions are executed.


    // The code block below basically reads a code file (e.g. "1.code.txt") and fills up 'instructions' vector.
    ifstream codeFile(this->codeFileName);      // Opens code file.
    string instName;        // 1st column of the code file.
    int instDuration;       // 2nd column of the code file.
    while(codeFile >> instName >> instDuration){
        instructions.push_back(instDuration);
    }
    codeFile.close();       // Closes code file.

}