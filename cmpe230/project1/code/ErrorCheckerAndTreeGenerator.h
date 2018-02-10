#ifndef PROJECT_CHECKSYNTAXERROR_H
#define PROJECT_CHECKSYNTAXERROR_H

#include <string>
#include <vector>
#include <stack>
#include "Node.h"

using namespace std;



/*
 * Parameter tokenVector    :   A vector of string which contains tokens.
 * Parameter root           :   A 'Node' pointer which will be the root of a binary statement tree.
 *
 * This function works as the driver of some other recursive functions.
 * As a whole, they check whether the tokens in 'tokenVector' are syntactically correct or not
 * according to a grammar. (Which is explained below.)
 * They also create a binary statement tree on the run while checking for syntax errors.
 * When an error is found, an exception is thrown which is caught in main() function.
 */
void checkErrorsAndGenerateTree(vector<string> &tokenVector, Node *&root);

#endif //PROJECT_CHECKSYNTAXERROR_H
