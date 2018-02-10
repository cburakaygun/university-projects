#include <iostream>
#include "ErrorCheckerAndTreeGenerator.h"

using namespace std;


/*
 * A binary statement tree is a binary expression tree such that
 * in addition to 4 binary operators ( '+' , '-' , '*' , '/' ),
 * it can also contain the assignment operator ('=').
 */



/*
 * An iterator for 'tokenVector'
 */
vector<string>::iterator currentToken;



/*
 * A stack of 'Node's which is used to create a binary statement tree.
 */
stack<Node*> nodeStack;

void statement();   bool expression();  bool term();    bool factor();  bool identifier();   bool number();



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
void checkErrorsAndGenerateTree(vector<string> &tokenVector, Node *&root){

    tokenVector.push_back( "!" );   // Adds this string at the end to mark the end of the line (tokens).
    currentToken = tokenVector.begin();

    statement();    // Invokes recursive functions.

    // After recursive functions are done and no errors are found,
    // the size of 'nodeStack' can be either 1 or 2 at this point.

    if (nodeStack.size() == 1) {    // If the size is 1, it means that the statement was just an expression.
        root = nodeStack.top();     // The stack now contains the root of the expression (statement) tree.
        nodeStack.pop();
    } else {    // If the size is 2, it means that the statement was an assignment.
        Node *expression = nodeStack.top(); // The top element of the stack is the root of the expression-part tree
        nodeStack.pop();
        Node *variable = nodeStack.top();   // The next element of the stack is just a Node which is the variable.
        nodeStack.pop();
        root = new Node(tokenVector[1], variable, expression);  // Create a Node the value which is '=' and which is the root of a binary statement tree.
    }

}



/*
 * The 6 functions below are mutually recursive and
 * they all together implement the following grammar which is in Extended Backus-Naor Form (EBNF)
 * (Terminals are expressed in '' and non-terminals are expressed in <>)
 *
 * <statement>  =>  [ <identifier>'=' ] <expression>
 * <expression> =>  <term> { ('+' | '-') <term> }
 * <term>       =>  <factor> { ('*' | '/') <factor> }
 * <factor>     =>  'identifier' | 'number' | '(' <expression> ')'
 *
 * 'num' is an integer.
 * 'id' name can contain only letters and/or digits. Also, it cannot begin with a digit.
 *
 * The algorithm below is a modified version of the parser found in the web page https://en.wikipedia.org/wiki/Recursive_descent_parser.
 * The modification is done to be able to detect some specific errors and construct a binary statement tree.
 *
 */



/*
 * This function implements the following part of the above grammar:
 * <statement>  =>  [ <identifier>'=' ] <expression>
 */
void statement(){

    bool isAssignment = false;  // A bool to indicate whether the statement is an assignment or just an expression.

    if( identifier() ){
        if( (*currentToken) == "=" ){
            ++currentToken;
            isAssignment = true;
        }else{  // Because identifier() consumes a token from 'tokenVector',
            --currentToken;     // the token must be restored for expression() to work properly.
            nodeStack.pop();    // The stack also must be restored to its previous state.
        }
    }

    if( expression() ){
        if( *currentToken == "!" ){     // If we have reached the end of the line (tokens)
            return;
        }else if( *currentToken == "="){    // if the line is in the form of <expression>'='
            throw string("invalid assignment");
        }else if( *currentToken == ")"){    // if the line is in the form of <expression>')'
            throw string("parenthesis mismatch");
        }else{  // if the line is in the form of <expression><expression>  or  <expression>'('
            string previousToken = *(--currentToken);
            throw string("missing <operator> between '" + previousToken + "' and '" + *(++currentToken) + "'");
        }
    }else{
        if( isAssignment ){     // if the line is in the form of  <identifier>'='<not_expression>
            throw string("<expression> expected after '='");
        }else{  // If the line begins with '+' , '-' , '*' , '/' , '=' or ')'
            throw string("there can't be '" + *currentToken + "' at the beginning of the line");
        }
    }

}



/*
 * This function implements the following part of the above grammar:
 * <expression>  =>  <term> { ('+' | '-') <term> }
 */
bool expression(){

    if( term() ){

        while( *currentToken == "+" || *currentToken == "-" ){
            string oprt = *currentToken;    // operator ( '+' or '-' )
            ++currentToken;
            if( !term() ){

                if( *currentToken == "!" )    // if the line ends with '+' or '-'
                    throw string("'" + oprt + "' at the end of the line");

                else if( *currentToken == "=" ) // If there exits a part in the form of [+][=]  or  [-][=]
                    throw string("'=' after '" + oprt + "'");

                else if( *currentToken == ")" ) // If there exits a part in the form of [+][)]  or  [-][)]
                    throw string("')' after '" + oprt + "'");
                else    // If there exits a part in the form of [+]<operator>  or  [-]<operator>
                    throw string("extra <operator> '" + *currentToken + "'");

            }

            Node *rightOperand = nodeStack.top();
            nodeStack.pop();
            Node *leftOperand = nodeStack.top();
            nodeStack.pop();
            nodeStack.push( new Node(oprt,leftOperand,rightOperand) );  // Creates a portion of the binary statement tree

        }

        return true;

    }

    return false;

}



/*
 * This function implements the following part of the above grammar:
 * <term>  =>  <factor> { ('*' | '/') <factor> }
 */
bool term(){

    if( factor() ){

        while( *currentToken == "*" || *currentToken == "/" ){
            string oprt = *currentToken;    // operator ( '*' or '/' )
            ++currentToken;
            if( !factor() ){

                if( *currentToken == "!" )      // if the line ends with '*' or '/'
                    throw string("'" + oprt + "' at the end of the line");

                else if( *currentToken == "=" ) // If there exits a part in the form of [*][=]  or  [/][=]
                    throw string("'=' after '" + oprt + "'");

                else if( *currentToken == ")" ) // If there exits a part in the form of [*][)]  or  [/][)]
                    throw string("')' after '" + oprt + "'");

                else    // If there exits a part in the form of [*]<operator>  or  [/]<operator>
                    throw string("extra <operator> '" + *currentToken + "'");

            }

            Node *right = nodeStack.top();
            nodeStack.pop();
            Node *left = nodeStack.top();
            nodeStack.pop();
            nodeStack.push( new Node(oprt,left,right) );    // Creates a portion of the binary statement tree

        }

        return true;

    }

    return false;

}



/*
 * This function implements the following part of the above grammar:
 * <factor>  =>  <identifier> | <number> | '(' <expression> ')'
 */
bool factor(){

    if( identifier() | number() )
        return true;

    else if( (*currentToken) == "(" ){

        ++currentToken;

        if( expression() ){

            if( (*currentToken) == ")" ){   // It it is in the form of '('<expression>')'
                ++currentToken;
                return true;
            }

            if( *currentToken == "=" )  // If there exits a part in the form of [(]<expression>[=]
                throw string("invalid assignment");

            else if( *currentToken == "!" ) // If the end of the line is in the form of [(]<expression>
                throw string("parenthesis mismatch");

            else{   // If there exits a part in the form of [(]<expression><variable>  or  [(]<expression><number>  or  [(]<expression>[(]
                string prevToken = *(--currentToken);
                throw string("missing <operator> between '" + prevToken + "' and '" + *(++currentToken));
            }

        }else   // If there exits a part in the form of [(]<not_expression>
            throw string("<expression> expected after '('");

    }

    return false;

}



/*
 * If the 'currentToken' currently points to a <identifier> token,
 * moves 'currentToken' to the next token and returns true.
 * Otherwise, returns false without moving 'currentToken'.
 */
bool identifier(){

    if( 65 <= (*currentToken)[0] ){     // If the first character of a token is a letter, it is an <identifier>
                                        // Considering the possible characters that can be found in the elements of 'tokenVector',
                                        // for a character's ASCII number being equal to or greater than 65 guarantees being a letter.
        nodeStack.push( new Node(*currentToken, nullptr, nullptr) );
        ++currentToken;
        return true;
    }

    return false;

}



/*
 * If the 'currentToken' currently points to a <number> token,
 * moves 'currentToken' to the next token and returns true.
 * Otherwise, returns false without moving 'currentToken'.
 */
bool number(){

    if( '0' <= (*currentToken)[0] && (*currentToken)[0] <= '9' ){ // If the first character of a token is a digit, it is a <number>
        nodeStack.push( new Node(*currentToken, nullptr, nullptr) );
        ++currentToken;
        return true;
    }

    return false;

}