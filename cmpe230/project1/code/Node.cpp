#include "Node.h"

using namespace std;

/*
 * Constructor of 'Node' struct.
 *
 * Parameter value      :   The string which contains the value of a token.
 * Parameter leftChild  :   The left child of this Node.
 * Parameter rightChild :   The right child of this Node.
 */
Node::Node( string &value , Node *leftChild , Node *rightChild ) {
    this->value = value;
    this->leftChild = leftChild;
    this->rightChild = rightChild;
}


/*
 * Destructor of 'Node' struct.
 */
Node::~Node() {
    delete leftChild;   // The left child of this Node.
    delete rightChild;  // The right child of this Node.
}
