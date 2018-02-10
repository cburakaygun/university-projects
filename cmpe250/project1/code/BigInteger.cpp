/*
Student Name: Cemal Burak Aygün
Student Number: 2014400072
Project Number: 1
Operating System: Xubuntu (Ubuntu 14.04) (on VirtualBox)
Compile Status: Compiles correctly
Program Status: Gives output correctly
Notes: Anything you want to say about your code that will be helpful in the grading process.

*/

#include "BigInteger.h"
#include <algorithm>

/*
 * number is written in BigInteger in reverse order.
 * For example, 1178 is represented as 8711 in BigInteger.
 * Every node in BigInteger contains one digit only.
 * Precondition: number >= 0
 */
BigInteger::BigInteger(int number){

    num = new LinkedList();

    while( number >= 10 ){
        num->pushTail(number%10);
        number = number/10;
    }

    num->pushTail(number);      // when we are here, it means that number >=0 and number < 10

}

/*
 * string bigInteger is written in BigInteger in reverse order.
 * For example, "1178" is represented as 8711 in BigInteger.
 * Precondition: The number represented in string bigInteger >= 0
 * If string bigInteger is empty (""), a BigInteger object that represents 0 is created.
 */
BigInteger::BigInteger(const string& bigInteger) {

    num = new LinkedList();
    if( bigInteger == "" ){
        num->pushHead(0);
    }else{
        for (char c : bigInteger) {
            num->pushHead(c - '0');        // Numbers are in order in ASCI codes (0, 1, ... , 9)
        }
    }

}

BigInteger BigInteger::operator+(const BigInteger &list){

    /*
     * This IF statement checks whether either of the operands is zero.
     * If only one of them is 0, it returns the one that is not 0.
     * If both of them are 0s, it returns a BigInteger object that represents 0.
     *
     * Since the numbers are represented in reverse order in BigInteger objects,
     * 0-valued tail means that the number is 0.
     */
    if( this->num->tail->data == 0 || list.num->tail->data == 0 ){
        if( this->num->tail->data != 0 ){
            return *this;
        }
        if( list.num->tail->data != 0 ){
            return list;
        }
        return BigInteger(0);
    }

    BigInteger resultBigInt;    // Creates a BigInteger object with a single node containing 0
    delete resultBigInt.num;    // Deletes the node (LinkedList) which was created in the previous step
    resultBigInt.num = new LinkedList( *(this->num) + *(list.num) );    // Uses operator+ in LinkedList.cpp

    /*
     * Since operator+ in LinkedList.cpp does not maintain the condition that every node contains one digit,
     * at this point resultBigInt may have some nodes that contain 2 digits.
     *
     * The codes below arrange resultBigInt such that every node contains one digit.
     *
     * Remember that numbers are represented in reverse order in BigInteger objects.
     */

    Node* currentNode = resultBigInt.num->head;
    int quotient = 0;       // Think this variable as "elde" in Turkish

    while( currentNode ){

        int currentData = currentNode->data + quotient;
        currentNode->data = currentData%10;     // Writes last digit of currentData in currentNode
        quotient = currentData/10;
        currentNode = currentNode->next;

    }

    if( quotient ){
        resultBigInt.num->pushTail(quotient);
    }

    return resultBigInt;

}


BigInteger BigInteger::operator*(const BigInteger &list){

    /*
     * When at least one of the operands of multiplication in integers is 0, the result is 0.
     *
     * Since the numbers are represented in reverse order in BigInteger objects,
     * 0-valued tail means that the number is 0.
     */
    if( list.num->tail->data == 0 || this->num->tail->data == 0){
        return BigInteger(0);
    }

    BigInteger resultBigInt;    // Creates a BigInteger object with a single node containing 0

    Node* currentList = list.num->head;
    Node* currentThis = this->num->head;

    int quotient = 0;       // Think this variable as "elde" in Turkish

    /*
     * while loop below multiplies the units digit of the parameter (list)
     * with this BigInteger (the whole number).
     *
     * It multiplies digit by digit:
     * First, (units digit of list) * (units digit of this)
     * Then, (units digit of list) * (tens digit of this)
     * Then, (units digit of list) * (hundreds digit of this)
     * etc.
     *
     * And it writes the result of multiplication in resultBigInt.
     * Remember that numbers are represented in reversed order in BigInteger objects
     * and every node contains one digit.
     */
    while( currentThis ){

        int currentData = currentList->data * currentThis->data + quotient;
        resultBigInt.num->pushTail(currentData%10);     // Adds a node to the tail which contains the last digit of currentData
        quotient = currentData/10;
        currentThis = currentThis->next;

    }

    if( quotient ){
        resultBigInt.num->pushTail(quotient);
        quotient = 0;
    }

    resultBigInt.num->head = resultBigInt.num->head->next;  // Deletes the node (containing 0) which is created when resultBigInt is created

    /*
     * while loop below multiplies the other digits (tens, hundreds, etc) of the parameter (list)
     * with this BigInteger (the whole number).
     *
     * It multiplies digit by digit:
     * First, (tens digit of list) * (units digit of this)
     * Then, (tens digit of list) * (tens digit of this)
     * Then, (tens digit of list) * (hundreds digit of this)
     * etc.
     * Then, (hundreds digit of list) * (units digit of this)
     * Then, (hundreds digit of list) * (tens digit of this)
     * Then, (hundreds digit of list) * (hundreds digit of this)
     * etc.
     *
     * It writes the result of multiplication of (X-digit of list) * this (the whole number) in tempBigInt.
     * Then, it adds the tempBigInt to the resultBigInt.
     *
     * Remember that numbers are represented in reversed order in BigInteger objects
     * and every node contains one digit.
     */

    int decimalCounter = 0;     // The number of 0s that added to the end of the result of (X-digit of list) * this (the whole number)

    while( currentList->next ){

        currentList = currentList->next;
        currentThis = this->num->head;
        BigInteger tempBigInt;

        while( currentThis ){

            int currentData = currentList->data * currentThis->data + quotient;
            tempBigInt.num->pushTail(currentData%10);       // Adds a node to the tail which contains the last digit of currentData
            quotient = currentData/10;
            currentThis = currentThis->next;

        }

        if( quotient ){
            tempBigInt.num->pushTail(quotient);
            quotient = 0;
        }

        /*
         * This for loop adds the necessary 0s to the end of the tempBigInt.
         * Since there is a 0 at the end (in the head node) of the tempBigInt when it is created,
         * decimalCounter starts from 0.
         */
        for( int i = 0 ; i < decimalCounter ; i++ ){
            tempBigInt.num->pushHead(0);
        }

        resultBigInt = resultBigInt + tempBigInt;
        decimalCounter++;

    }

    return resultBigInt;

}


BigInteger BigInteger::operator*(int i){

    return (*this) * BigInteger(i);

}


BigInteger::BigInteger(const BigInteger &other){

    this->num = new LinkedList(*(other.num));
}


BigInteger& BigInteger::operator=(const BigInteger &list){

    delete this->num;
    this->num = new LinkedList( *(list.num) );
    return *this;

}


BigInteger::~BigInteger(){
    delete num;
}