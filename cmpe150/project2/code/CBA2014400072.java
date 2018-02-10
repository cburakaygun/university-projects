/*
CmpE 150 Introduction to Computing, Fall 2015
Project 2
CEMAL BURAK AYGÜN, 2014400072
*/

import java.util.Scanner;
public class CBA2014400072 {



    public static void main(String[] args){

        Scanner keyboard = new Scanner(System.in);
        String line = "ABGGRTFPPKVIGVJ*";   // line is the combination of 4 rows of the board in a single line and it's initial value is the default board configuration        
        System.out.print("WELCOME TO THIS WEIRD GAME OF P*G \nDo you want to use the default board configuration? (YES/NO) ");
        String choice = keyboard.next();   // choice is the user's choice about using the default board configuration or not
        
        /* The IF STATEMENT below executes if the user's choice above is "No". It takes 4 rows from the user and updates the String line. */    
        if(choice.equalsIgnoreCase("NO")){
           
            System.out.print("\n>> You are going to type 4 characters for each row. (16 characters in total.)\n>> Do NOT type any spaces between any two characters and make sure that you write exactly 1 * besides 15 other characters.\n\nWrite four characters for the 1st row:   ");
            String line1 = keyboard.next();   // line1 is the first row of the board
            
            System.out.print("Write four characters for the 2nd row:   ");
            String line2 = keyboard.next();   // line2 is the second row of the board
            
            System.out.print("Write four characters for the 3rd row:   ");
            String line3 = keyboard.next();   // line3 is the third row of the board
            
            System.out.print("Write four characters for the 4th row:   ");
            String line4 = keyboard.next();   // line4 is the fourth row of the board
            
            line = (line1 + line2 + line3 + line4).toUpperCase();   // line gets updated
            
        }            

        System.out.print(boardDrawing(line) + "How many moves do you want to make? ");
        int totalMove = keyboard.nextInt();   // totalMove is the number of moves the user has in the game
        
        System.out.println("\n>> Make a move and press ENTER.\n>> After each move, the board configuration and your total points will be printed.\n>> Type L for Left, R for Right, U for Up, and D for Down.");       
        int curScore = 0;   // curScore is the current score the user has gained
       
        /* The FOR LOOP below limits the game to the number of moves the user has */
        for(int move = 1 ; move <= totalMove ; move++){   // move is the current move the user is in
            
            System.out.print("\nMove#" + move + " (L/R/U/D) : ");
            String direction = keyboard.next();   // direction is the direction in which the * moves
                        
            /* The IF STATEMENT below moves the * to the left if the * is not at the leftmost side of the row */
            if(direction.equalsIgnoreCase("L") && line.indexOf("*") % 4 != 0){             
               
                char c = line.charAt(line.indexOf("*") - 1);   // c is the character on the left side of the *
                curScore += score(c);   // scoreSoFar gets updated
                c = pG(c);   // c gets updated
                line = line.substring(0,line.indexOf("*")-1) + "*" + c + line.substring(line.indexOf("*") + 1);   // line gets updated
                         
            /* The ELSE IF STATEMENT below moves the * to the right if the * is not at the rightmost side of the row */
            }else if(direction.equalsIgnoreCase("R") && line.indexOf("*") % 4 != 3){
              
                char c = line.charAt(line.indexOf("*") + 1);   // c is the character on the right side of the *
                curScore += score(c);   // scoreSoFar gets updated
                c = pG(c);   // c gets updated
                line = line.substring(0,line.indexOf("*")) + c + "*" + line.substring(line.indexOf("*") + 2);   // line gets updated
            
            /* The ELSE IF STATEMENT below moves the * up if the * is not at the topmost side of the column */            
            }else if(direction.equalsIgnoreCase("U") && !(line.indexOf("*") < 4)){
                
                char c = line.charAt(line.indexOf("*") - 4);   // c is the character on the top side of the *
                curScore += score(c);   // scoreSoFar gets updated
                c = pG(c);   // c gets updated
                line = line.substring(0,line.indexOf("*") - 4) + "*" + line.substring(line.indexOf("*") - 3, line.indexOf("*")) + c + line.substring(line.indexOf("*") + 1);   // line gets updated
                                        
            /* The ELSE IF STATEMENT below moves the * down if the * is not at the bottommost side of the column */
            }else if(direction.equalsIgnoreCase("D") && !(line.indexOf("*") > 11)){
            
                char c = line.charAt(line.indexOf("*") + 4);   // c is the character on the bottom side of the *
                curScore += score(c);   // scoreSoFar gets updated
                c = pG(c);   // c gets updated
                line = line.substring(0,line.indexOf("*")) + c + line.substring(line.indexOf("*") + 1, line.indexOf("*") + 4) + "*" + line.substring(line.indexOf("*") + 5);   // line gets updated
                                
            }
                
            System.out.print(boardDrawing(line) + "Current Score: " + curScore + "\n");
                    
        }
        
        System.out.print("\nEND OF THE GAME\nThank you for playing this game.");
                
    }
    
    
    
    /*
    The method below updates the character that swaps with the *.
    It changes the character to I if it is P or G.
    It leaves the character unchanged if it is neither P nor G.
    */
    public static char pG(char c){
       
        if(c == 'P' || c == 'G'){
            
            return 'I';
            
        }else{
        
            return c;
            
            }
        
    }
    
    
    
    /*
    The method below calculates the score that the user gains from the last move.
    It returns 1 (points) if * swaps with P, 5 (points) for swapping with G and 0 (points) for swapping with other characters.
    */
    public static int score(char c){
       
        if(c == 'P'){
            
            return 1;
            
        }else if(c == 'G'){
            
            return 5;
            
            }else{
            
                return 0;
                
                }
                
    }
        
        
        
    /*
    The method below draws the current board configuration.
    It gets the current configuration as a single line and divides it after every four characters from the beginning of the line.
    At the end, it returns the configuration as a 4-line table.
    */
    public static String boardDrawing(String line){
    
       return "\nThis is the board configuration now: \n\n" + line.substring(0,4) + "\n" + line.substring(4,8) + "\n" + line.substring(8,12) + "\n" + line.substring(12) + "\n\n";
    
    }
    
    
       
}