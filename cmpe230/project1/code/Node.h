#ifndef PROJECT_NODE_H
#define PROJECT_NODE_H

#include <string>

using namespace std;

/*
 * Representation of a node of a binary statement tree.
 *
 * A binary statement tree is a binary expression tree such that
 * in addition to 4 binary operators ( '+' , '-' , '*' , '/' ),
 * it can also contain the assignment operator ('=').
 *
 * The leaves of the binary statement tree can only contain <variable> or <number>.
 * The nodes other than leaves can only contain '+' , '-' , '*' , '/' or '='.
 */
struct Node{

    string value;       // The string which contains the value of a token.
    Node *leftChild;    // The left child of this Node.
    Node *rightChild;   // The right child of this Node.


    /*
     * Constructor of 'Node' struct.
     *
     * Parameter value      :   The string which contains the value of a token.
     * Parameter leftChild  :   The left child of this Node.
     * Parameter rightChild :   The right child of this Node.
     */
    Node( string &value , Node *leftChild , Node *rightChild );
    

    /*
     * Destructor of 'Node' struct.
     */
    ~Node();

};

#endif //PROJECT_NODE_H
