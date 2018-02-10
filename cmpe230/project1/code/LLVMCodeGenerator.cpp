#include <iostream>
#include "LLVMCodeGenerator.h"

using namespace std;

string *allocText;  // A pointer that points to 'allocateText' in main() which stores the LLVM instructions about allocating variables.
string *instText;   // A pointer that point to 'instructionText' in main() which stores the LLVM instructions about other operations.
int *tempInt;  // A pointer that points to 'tempVarIndex' in main() which stores the integer part of the name of the temporary variables in LLVM code.



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
string generateCodeRecursively(Node *&node){

    if( node->value == "+" || node->value == "-" || node->value == "*" || node->value == "/" ){

        string leftOperand = generateCodeRecursively(node->leftChild);
        string rightOperand = generateCodeRecursively(node->rightChild);

        string operation;
        if( node->value  == "+" )
            operation = "add";
        else if( node->value  == "-" )
            operation = "sub";
        else if( node->value  == "*" )
            operation = "mul";
        else
            operation = "sdiv";

        *instText += "%" + to_string(*tempInt) + " = " + operation + " i32 " + leftOperand + "," + rightOperand + "\n";    // LLVM code for arithmetic operation

        return "%" + to_string( (*tempInt)++ );    // returns the name of the temporary variable which is created above and increments 'tempInt' by 1 for the next temporary variable name.

    }else if( node->value == "=" ){

        string expression = generateCodeRecursively(node->rightChild);  // The right child of '=' is assigned to the left child of '='

        if( allocText->find( node->leftChild->value ) == string::npos ){    // If the left child of '=' has not allocated before,
            *allocText += "%" + node->leftChild->value + " = alloca i32\n"; // allocates it.
        }

        *instText += "store i32 " + expression + ", i32* %" + node->leftChild->value + "\n";    // LLVM code for assignment

        return "=";

    }else if( 65 <= node->value[0] ){   // if this 'Node' contains an <identifier> name

        if( allocText->find( node->value ) == string::npos )  // If <identifier> was not allocated,
            throw string("undefined varible " + node->value);   // gives error.

        *instText += "%" + to_string(*tempInt) + " = load i32* %" + node->value + "\n";    // Else, copies the value of <identifier> into a temporary variable.

        return "%" + to_string( (*tempInt)++ );    // returns the name of the temporary variable which is created above and increments 'tempInt' by 1 for the next temporary variable name.

    }else  // If this 'Node' contains a <number> name.
        return node->value;     // Just returns the value of <number>

}



/*
 * Parameter node               :   A pointer to a Node which is the node of a binary statement tree.
 * Parameter allocateText       :   A string which stores the LLVM instructions about allocating variables.
 * Parameter instructionText    :   A string which stores the LLVM instructions about other operations. (arithmetic operations, assignment, print etc.)
 * Parameter tempVarIndex       :   An integer which stores the integer part of the name of the temporary variables in LLVM code.
 *
 * This function instantiates the pointers with its parameters and works as the driver of 'generateCodeRecursively'.
 * It also stores the LLVM code for printing into 'instructionText' if necessary.
 */
void generateLLVMCode(Node *&root, string &allocateText, string &instructionText, int &tempVarInt){

    allocText = &allocateText;
    instText = &instructionText;
    tempInt = &tempVarInt;

    // There are 3 possible types of string that can be returned from 'generateCodeRecursively' below.
    // These are: "=" , <number> or <LLVMTemporaryVariableName> (which is in the form of %<integer>)
    string lastItem = generateCodeRecursively(root);

    // If 'lastItem' is "=", it means that the statement was an assignment. In this case, nothing more needed to be done.
    // If 'lastItem' is <number> or <LLVMTemporaryVariableName>, it means that the statement was an expression and
    // the result of this expression is 'lastItem' and it needs to be printed.

    if( lastItem != "=" ){

        // LLVM code for printing
        instructionText += "call i32 (i8*, ...)* @printf(i8* getelementptr ([4 x i8]* @print.str, i32 0, i32 0), i32 " + lastItem + " )\n";

        ++tempVarInt;     // In LLVM code, after print instruction the integer part of the next temporary variable should be incremented by 2 instead of 1.
                            // 'generateCodeRecursively' increments it by 1 before it terminates. So, we should increment it one more time here.


    }

}