#ifndef PROJECT_TOKENIZER_H
#define PROJECT_TOKENIZER_H

#include <string>
#include <vector>
#include "Tokenizer.h"

using namespace std;


/*
 * Parameter line           :   A string (which is a line from file.stm)
 * Parameter tokenVector    :   An empty vector which will contain the tokens from 'line'.
 *
 * This function reads 'line' character by character and inserts the tokens into 'tokenVector'.
 * Possible tokens are as follows: <identifier> , <number> , '+' , '-' , '*' , '/' , '=' , '(' and ')'.
 * It also has some error detecting capabilities. Those are:
 *  1) Invalid Character Detection
 *      Any character other than digits, English letters, space characters, '+' , '-' , '*' , '/' , '=' , '(' and ')' is considered as invalid.
 *  2) Invalid Variable Name Detection
 *      A variable name cannot start with a digit.
 *
 * If it detects an error, it throws an exception which is caught in main() function.
 */
void tokenize(string &line, vector<string> &tokenVector);

#endif //PROJECT_TOKENIZER_H