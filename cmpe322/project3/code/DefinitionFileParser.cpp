#include <fstream>
#include "DefinitionFileParser.h"

/*
 * Parses "definition.txt" file and using the information in that file
 * creates 'Process'es and adds them into 'processQueue'.
 */
void parseDefinitionFile(queue<Process> *processQueue){
    ifstream definitionFile("definition.txt");      // Opens "definition.txt" file.
    string processName;             // 1st column of "definition.txt" file.
    string processCodeFileName;     // 2nd column of "definition.txt" file.
    int processArrivalTime;         // 3rd column of "definition.txt" file.
    while (definitionFile >> processName >> processCodeFileName >> processArrivalTime) {
        processQueue->push(Process(processName, processCodeFileName, processArrivalTime));
    }
    definitionFile.close();     // Closes "definition.txt" file.
}