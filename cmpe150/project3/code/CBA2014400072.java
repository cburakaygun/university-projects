/*
CmpE 150 Introduction to Computing, Fall 2015
Project 3
CEMAL BURAK AYGÜN, 2014400072
*/

import java.io.*;
import java.util.*;

public class CBA2014400072 {


    public static void main(String[] args) throws FileNotFoundException{
    
        char[][] cell = new char[18][18];   // 18x18 board and every cell[][] is a cell of that board        
        
        /* The FOR LOOP below writes O in the cells in the first and last rows and the first and last columns of the 18x18 board */
        for(int i = 0 ; i < 18 ; i ++){    // i is the row and column number of the board        
            cell[0][i] = 'O';   // first row
            cell[17][i] = 'O';   //last row
            cell[i][0] = 'O';   // first column
            cell[i][17] = 'O';   // last column        
        }        
       
        Scanner file = new Scanner(new File("input.txt"));        
       
        /* The nested FOR LOOPs below copy every single character of the input.txt to the elements of cell[][] */
        for(int row = 0 ; row < 16 ; row ++){   // row is the row number of the board        
            String line = file.next().toUpperCase();   // line is a row of the board            
            for(int clmn = 0 ; clmn < 16 ; clmn++){   // clmn is the column number of the board                
                cell[row+1][clmn+1] = line.charAt(clmn);            
            }        
        }        
        
        printBoard(cell, 'i');   // i for initial       
        boolean cont = true;   // cont is continue, it determines whether the WHILE LOOP runs or stops        
       
        while( cont ){           
          
           cont = false;          
           char[][] temp = new char[18][18];   // temp is temporary, it is an Array to check whether any of the elements of cell[][] has changed           
         
           /* The nested FOR LOOPs below copy cel[][] into temp[][] */
           for(int row = 1 ; row <= 16 ; row++){   // row is the row number            
                for(int clmn = 1 ; clmn <= 16 ; clmn++){   // clmn is the column number                
                    temp[row][clmn] = cell[row][clmn];                
                }                
            }            
           
            /* The nested FOR LOOPs below specify the cell */
            for(int row = 1 ; row <= 16 ; row++){   // row is the row number            
                for(int clmn = 1 ; clmn <= 16 ; clmn++){   // clmn is the column number                    
                    
                    /* The IF STATEMENT below runs darkColor method if the cell is Dark Color */
                    if( isDarkColor(cell, row, clmn) ){                    
                        darkColor(cell, row, clmn);                    
                    /* The IF STATEMENT below runs white method if the cell is White */
                    }else if( cell[row][clmn] == 'W' ){                    
                        white(cell, row, clmn);                    
                    /* The IF STATEMENT below runs orange method if the cell is Orange */                
                    }else if( cell[row][clmn] == 'O' ){                    
                        orange(cell, row, clmn);                    
                    } 
                                   
                }            
            }               
           
            /* The nested FOR LOOPs below specify the cell */
            for(int row = 1 ; row <= 16 ; row++){   // row is the row number            
                for(int clmn = 1 ; clmn <= 16 ; clmn++){   // clmn is the column number                
                   
                    /* The IF STATEMENT below makes cont true if a cell's color has changed */                
                    if( temp[row][clmn] != cell[row][clmn] ){                    
                        cont = true;                    
                    }                
                }                                
            } 
                   
        }        
      
        System.out.println();
        System.out.println();        
       
        /* The nested FOR LOOPs below specify the cell */
        for(int row = 1 ; row <= 16 ; row++){   // row is the row number            
            for(int clmn = 1 ; clmn <= 16 ; clmn++){   // clmn is the column number                
                
                /* The IF STATEMENT below changes the color of the cell to (L)ight Blue if it is (W)hite */
                if( cell[row][clmn] == 'W' ){                    
                    cell[row][clmn] = 'L';                        
                }   
                             
            }                
        }        
        
        printBoard(cell, 'f');   // f for final    
        
    }    
    
    /* 
    The method below processes the Dark Color cells.
    It examines the 4 neighbour cells (up, down, left and right) of the Dark Color cell and changes it according to some rules.
    */
    public static void darkColor(char[][] cell, int row, int clmn){    
       
        int numDC = 0, numLC = 0, numP = 0;   // number of (D)ark (C)olor, (L)ight (C)olor and (P)urple cells        
       
        if( isDarkColor(cell, row-1, clmn) ){        
            numDC++;        
        }else{        
            numLC++;        
        }
        if( cell[row-1][clmn] == 'P' ){        
            numP++;        
        }   
        
        if( isDarkColor(cell, row+1, clmn) ){        
            numDC++;        
        }else{        
            numLC++;        
        }
        if( cell[row+1][clmn] == 'P' ){        
            numP++;        
        }     
        
        if( isDarkColor(cell, row, clmn-1) ){        
            numDC++;        
        }else{        
            numLC++;        
        }
        if( cell[row][clmn-1] == 'P' ){
        
            numP++;
        
        }
        
        if( isDarkColor(cell, row, clmn+1) ){        
            numDC++;        
        }else{        
            numLC++;        
        }
        if( cell[row][clmn+1] == 'P' ){        
            numP++;        
        }
        
        if( numDC == 4 ){        
            cell[row][clmn] = 'B';        
        }else if( numLC == 3 || (numLC == 2 && numP >= 1) || (numLC == 1 && numP >= 2) ){        
            cell[row][clmn] = 'P';        
        }else if( cell[row][clmn] != 'B' && cell[row][clmn] != 'P' ){        
            cell[row][clmn] = 'C';        
        }
    
    }    
    
    /* 
    The method below processes the White cells.
    It examines the 4 neighbour cells (up, down, left and right) of the White cell and changes it according to some rules.
    */
    public static void white(char[][] cell, int row, int clmn){
    
        if( cell[row-1][clmn] == 'O' || cell[row+1][clmn] == 'O' || cell[row][clmn-1] == 'O' || cell[row][clmn+1] == 'O' ){        
            cell[row][clmn] = 'O';        
        }
    
    }    
   
    /* 
    The method below processes the Orange cells.
    It examines the 4 neighbour cells (up, down, left and right) of the Orange cell and changes it according to some rules.
    */
    public static void orange(char[][] cell, int row, int clmn){
    
        int numY = 0, numO = 0, numDC = 0;   // number of (Y)ellow, (O)range and (D)ark (C)olor cells
        
        if( isDarkColor(cell, row-1, clmn) ){        
            numDC++;        
        }else if( cell[row-1][clmn] == 'Y' ){        
            numY++;        
        }else if( cell[row-1][clmn] == 'O' ){        
            numO++;        
        }
        
        if( isDarkColor(cell, row+1, clmn) ){        
            numDC++;        
        }else if( cell[row+1][clmn] == 'Y' ){        
            numY++;        
        }else if( cell[row+1][clmn] == 'O' ){        
            numO++;        
        }
        
        if( isDarkColor(cell, row, clmn-1) ){        
            numDC++;        
        }else if( cell[row][clmn-1] == 'Y' ){        
            numY++;        
        }else if( cell[row][clmn-1] == 'O' ){        
            numO++;        
        }
        
        if( isDarkColor(cell, row, clmn+1) ){
        
            numDC++;
        
        }else if( cell[row][clmn+1] == 'Y' ){        
            numY++;        
        }else if( cell[row][clmn+1] == 'O' ){        
            numO++;        
        }
        
        if( (numY >= 2 && numO <= 1) || (numY == 1 && numDC >= 2) || (numDC >= 2 && numO >= 1) ){        
            cell[row][clmn] = 'Y';        
        }
        
    }    
    
    /* The method below checks whether a cell is Dark Color and returns true if so */
    public static boolean isDarkColor(char[][] cell, int row, int clmn){
    
        return cell[row][clmn] == 'G' || cell[row][clmn] == 'B' || cell[row][clmn] == 'P' || cell[row][clmn] == 'C';
    
    }    
    
    /*
    The method below prints the board configuration and the statistics of the cells about their colors.
    To print the INITIAL statistics, the parameter board should be i.
    */
    public static void printBoard(char[][] cell, char board){
        
        /* The nested FOR LOOPs below print the 16x16 board configuration */
        for(int row = 1 ; row <=16 ; row++){   // row is the row number        
            for(int clmn = 1 ; clmn <=16 ; clmn++){   // clmn is the column number            
                System.out.print(cell[row][clmn]);            
            }            
            System.out.println();        
        }
        
        /* The IF STATEMENT below executes for the INITIAL statistics of the colors */
        if( board == 'i' ){
        
            int w = 0, g = 0;   // number of (w)hite and (g)ray cells
            
            /* The nested FOR LOOPs below specify the cell */
            for(int row = 1 ; row <= 16 ; row++){   // row is the row number            
                for(int clmn = 1 ; clmn <= 16 ; clmn++){   // clmn is the column number
                
                    /* The IF STATEMENT below increases w by 1 if the cell is White */
                    if( cell[row][clmn] == 'W' ){                    
                        w++;                    
                    /* The ELSE STATEMENT below increases g by 1 if the cell is Gray */
                    }else if( cell[row][clmn] == 'G' ){                    
                        g++;                        
                    }
                
                }                
            }
            
            System.out.print("INITIAL: G=" + g + " W=" + w + " ALL=" + (w+g) );
        
        /* The ELSE STATEMENT below executes for the FINAL statistics of the colors */
        }else{        
            int p = 0, c = 0, b = 0, o = 0, y = 0, l = 0;   // number of (p)urple, (c)hocolate, (b)lack, (o)range, (y)ellow and (l)ight blue cells
            
            /* The nested FOR LOOPs below specify the cell */
            for(int row = 1 ; row <= 16 ; row++){   // row is the row number            
                for(int clmn = 1 ; clmn <= 16 ; clmn++){   // clmn is the column number
                    
                    /* The IF STATEMENT below increases p by 1 if the cell is Purple */
                    if( cell[row][clmn] == 'P' ){                    
                        p++;                    
                    /* The IF STATEMENT below increases c by 1 if the cell is Chocolate */
                    }else if( cell[row][clmn] == 'C' ){                    
                        c++;                    
                    /* The IF STATEMENT below increases b by 1 if the cell is Black */
                    }else if( cell[row][clmn] == 'B' ){                    
                        b++;                    
                    /* The IF STATEMENT below increases o by 1 if the cell is Orange */
                    }else if( cell[row][clmn] == 'O' ){                    
                        o++;                    
                    /* The IF STATEMENT below increases y by 1 if the cell is Yellow */
                    }else if( cell[row][clmn] == 'Y' ){                    
                        y++;                    
                    /* The ELSE STATEMENT below increases l by 1 if the cell is Light Blue */
                    }else if( cell[row][clmn] == 'L' ){                    
                        l++;                        
                    }
                
                }                
            }
            
            System.out.print("EXPLORED: P=" + p + " C=" + c + " B=" + b + " O=" + o + " Y=" + y + " L=" + l + " ALL=" + (p+c+b+o+y+l) );
        
        }
    
    }
    
    
}