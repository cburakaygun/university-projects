#include "Tokenizer.h"

using namespace std;

/*
 * Parameter c  :   A character
 * Returns      :   If c is a digit, true. Otherwise, false.
 */
bool isDigit(char &c) {
    return '0' <= c && c <= '9';
}


/*
 * Parameter c  :   A character
 * Returns      :   If c is an uppercase or lowercase letter from English alphabet, true. Otherwise, false.
 */
bool isLetter(char &c) {
    return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
}

/*
 * Parameter c  :   A character
 * Returns      :   If c is in the interval [0, 32] in ASCII table, true. Otherwise, false.
 *                  This interval contains space characters like ' ' , '\n' and '\r'
 */
bool isIgnored(char &c){
	return 0 <= c && c <= 32;
}

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
void tokenize(string &line, vector<string> &tokenVector){

    string::iterator currentChar = line.begin();

    while( currentChar != line.end() ){

        if (*currentChar == '+' || *currentChar == '-' || *currentChar == '*' || *currentChar == '/' ||
                   *currentChar == '=' || *currentChar == '(' || *currentChar == ')') {

            tokenVector.push_back( string(1, *currentChar) );
            ++currentChar;

        } else if ( isDigit(*currentChar) ){

            string token = "";      // it expects a number token

            do {
                token += string(1, *currentChar);
            } while (++currentChar != line.end() && isDigit(*currentChar));

            if (currentChar != line.end() && isLetter(*currentChar)) {  // If there is a letter after some digits...

                do{     // continue getting those characters for details of error message.
                    token += string(1, *currentChar);
                }while(++currentChar != line.end() && (isDigit(*currentChar) || isLetter(*currentChar)));

                throw string("invalid <variable> name '" + token + "'' Valid syntax for <variable> name in (EBNF) form is <letter>{<letter>|<digit>}");

            } else  // If there are any characters other than a letter after some sequential digits...
                tokenVector.push_back(token);   // considers those digits (as a whole) as a number token

        } else if (isLetter(*currentChar)) {

            string token = "";

            do {        // getting variable token
                token += string(1, *currentChar);
            } while (++currentChar != line.end() && (isDigit(*currentChar) || isLetter(*currentChar)));

            tokenVector.push_back(token);

        }else if( isIgnored(*currentChar) ){

            ++currentChar;

        }else{

            throw string("invalid character " + string(1,*currentChar));

        }

    }

}