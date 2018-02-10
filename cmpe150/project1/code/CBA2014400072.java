/*
CmpE 150 Introduction to Computing, Fall 2015
Project 1
CEMAL BURAK AYGÜN, 2014400072
*/

public class CBA2014400072 {

    public static final int N = 4;   // A positive integer constant that is used in multiplicationTable() and sequence() methods
    public static final int M = 23415;   // An integer constant that is used in sequence() method

    public static void main(String[] args) {
        
        /* Prints a title for the multiplicationTable() method for a better looking output :) */
        System.out.println("##############################\n###  MULTIPLICATION TABLE  ###\n##############################\n");
        multiplicationTable();
        
        /* Prints a title for the sequence() method and the values of M and N in parantheses for a better looking output :) */
        System.out.println("\n\n\n##################\n###  SEQUENCE  ###\n##################\n(M = "+M+", N = "+N+")\n");
        sequence();
                
    }
    
    /* 
    The method below prints a N * N dimensional multiplication table, N is declared at the top of the program.
    It leaves the elements that stay at upper triangular region of the table blank for kids to fill in.
    */
    public static void multiplicationTable() {
    
        /* The loop below determines the number of rows of the table */
        for(int row = 1; row <= N; row++) {   // row is the row number
           
            /* The loop below prints the elements in the specific row except the ones that stay at upper triangular side of the table */
            for(int column = 1; column <= row; column++) {   // column is the column number
                System.out.print(row * column + "\t");
            }
            
            /* The loop below prints brackets in the specific row instead of the elements that stay at upper triangular side of the table */
            for(int bracket = 1; bracket <= N - row; bracket++) {   // bracket is the bracket number
                System.out.print("[   ]\t");
            }
            
            /* Goes to new row */
            System.out.println();
        
        }
    
    }
    
    /*
    The method below prints a sequence that has following properties:
    # It has 4 elements
    # Each element is equal to [the number of digits in the previous element] times [N, which is declared at the top of the program]
    # First element uses M, which is declared at the top of the program, as the 'previous element'
    */
    public static void sequence() {
    
        int previousNumber = M;   // A variable that is used to calculate the elements of the sequence 
        int number;   // number is the element of the sequence
        int digitNumber;   // A variable that stores the number of digits in previousNumber
            
        /* Limits the sequence with 4 elements */
        for(int size = 1; size <= 4; size++) {    // size is the number of elements of the sequence
            
            digitNumber = 1;   // Sets digitNumber to 1 which is the number of digits in zero
            
            /* Calculates the number of digits in previousNumber for positive numbers */
            for(; previousNumber >= 10; previousNumber /= 10) {
                digitNumber++;   // After the loop, the value of digitNumber becomes the number of digits in positive previousNumber
            }
            
            /* Calculates the number of digits in previousNumber for negative numbers */
            for(; previousNumber <= -10; previousNumber /= 10) {
                digitNumber++;   // After the loop, the value of digitNumber becomes the number of digits in negative previousNumber
            }
            
            number = digitNumber * N;   // number gets updated, it becomes [the number of digits in previousNumber] times [N]
            
            /* Prints number as the element of the sequence and goes to new line */
            System.out.println(number);
            
            previousNumber = number;   // previousNumber gets updated, number becomes the 'previous element' for the next element of the sequence
            
        }
    
    }
    
}