#ifndef DEFINITIONFILEPARSER_H
#define DEFINITIONFILEPARSER_H

#include <queue>
#include "Process.h"

/*
 * Parses "definition.txt" file and using the information in that file
 * creates 'Process'es and adds them into 'processQueue'.
 */
void parseDefinitionFile(queue<Process> *processQueue);

#endif