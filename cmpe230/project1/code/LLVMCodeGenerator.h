#ifndef PROJECT_PARSETREEEVALUATOR_H
#define PROJECT_PARSETREEEVALUATOR_H

#include "Node.h"
#include <string>

using namespace std;



/*
 * Parameter node   :   the node of a binary statement tree
 *
 * This function goes through a binary statement tree in post order manner.
 * Instead of evaluating the results of expressions and/or assignments, it generates LLVM code corresponding to those operations step by step.
 * It uses 'tempInt' for generating temporary variable names (e.g. %4) and
 * stores generated code segments in 'alllocText' and 'instText'
 *
 * This function also can detect "undefined variable" errors.
 * When an error is detected, an exception is thrown which is caught in main().
 */
void generateLLVMCode(Node *&root, string &allocateText, string &instructionText, int &tempVarInt);

#endif //PROJECT_PARSETREEEVALUATOR_H
