#include <iostream>
#include <fstream>
#include <string>
#include "Node.h"
#include "Tokenizer.h"
#include "ErrorCheckerAndTreeGenerator.h"
#include "LLVMCodeGenerator.h"

using namespace std;

int main(int argc , char* argv[]) {

    // Checks whether the program is executed with 1 argument or not.
    if( argc < 2 ) {
        cout << "Run the code with the following command: ./stm2ir [input_file]" << endl;
        return 1;
    }

    string allocateText = "";   // Stores the LLVM instructions about allocating variables.
    string instructonText = ""; // Stores the LLVM instructions about other operations such as assignment, arithmetic calculations, etc.
    int tempVarInt = 1;     // Stores the integer part of the name of the temporary variables in LLVM code.

    int lineNumber = 0;     // The number of the line in the 'inputFile'

	ifstream inputFile( argv[1] );  // Opens input file.

    string line;    // The line of the input file.

    while( getline(inputFile , line) ){

        ++lineNumber;

        if( line.size() == 0 )
            continue;

        try{

            vector<string> tokenVector; // Stores the tokens of 'line'.
            tokenize( line , tokenVector );

            Node *root;     // The root of the statement tree which is generated in 'checkErrorsAndGenerateTree' below.
            checkErrorsAndGenerateTree( tokenVector , root );

            generateLLVMCode(root, allocateText, instructonText, tempVarInt);

            delete root;    // Deletes the Nodes of the binary statement tree which was constructed for 'line'.

        }catch( string &errorText ){

            cout << "Error: Line " + to_string(lineNumber) + ": " + errorText + "\n";
            return 0;

        }

    }

    inputFile.close();

    string outputFileName = argv[1];    // Gets the full name of the input file.
    size_t dotIndex = outputFileName.find_first_of(".");    // Gets the index of the first appearing '.'.
    if( dotIndex == string::npos )  // If input file does not contain a '.',
        outputFileName += ".ll";    // adds ".ll" to the end of the name.
    else
        outputFileName.replace( dotIndex+1 , outputFileName.size() , "ll" );    // Deletes everything after first '.' and adds "ll".


    ofstream outputFile( outputFileName );     // Opens output file.

    // Writes LLVM codes to the output file.
    outputFile << "; ModuleID = 'stm2ir'\n";
    outputFile << "declare i32 @printf(i8*, ...)\n";
    outputFile << "@print.str = constant [4 x i8] c\"%d\\0A\\00\"\n\n";
    outputFile << "define i32 @main() {\n";
    outputFile << allocateText;
    outputFile << instructonText;
    outputFile << "ret i32 0\n";
    outputFile << "}";

    outputFile.close();

    return 0;

}