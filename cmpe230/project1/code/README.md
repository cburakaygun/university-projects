## SECTION 1: PROBLEM DESCRIPTION

We are given a file which contains some statements, one statement on a line.
A statement can be either an assignment or an expression the result of which to be printed.

Our task is to develop a program which translates those statements to corresponding **LLVM** code.
The program should input the given file and produce an output file that contains **LLVM** code.
When there is a syntax error or an use of undefined variable on the given statement file, the program should exit without generating
**LLVM** code (when it encounters the first error) and write an error message on the standard output stream.

The error message will have the following template:
`Error: Line <number>: <message>`

We are given the grammar below for error checking (in **BNF**):
Terminals are represented in `'` and non-terminals are represented in `<` and `>`.

```
<expr>  	=>  <term> <moreterms>
<moreterms>	=>  '+' <term> <moreterms>  |  '-' <term> <moreterms>  |  E
<term>		=>  <factor> <morefactors>
<morefactors>	=>  '*' <factor> <morefactors>  |  '/' <factor> <morefactors>  |  E
<factor>	=>  '(' <expr> ')'  |  'id'  |  'num'
```

_num_ is an integer.

_id_ name can contain only letters and/or digits. Also, it cannot begin with a digit.

_E_ 	is epsilon which represents the empty string.
		

## SECTION 2: PROBLEM SOLUTION (USING C++11)

To be able to produce the correct **LLVM** code, we should examine each statement given in the input file,
understand what the statement does and produce the coresponding **LLVM** code for that statement.
In other words, we should translate every single statement into **LLVM** code segments.

Since each line contains one statement, it is reasonable to process the input file line by line.

I divided my program into 4 parts which basically does the followings:

* In the first part, I read a line from the input file.
* In the second part, I tokenize the line into a vector of strings.
* In the third part, I construct a binary statement tree using those tokens. A binary statement tree is a binary expression tree such that
in addition to 4 binary operators (**+**, **-**, __*__, **/**) it can also contain the assignment operator (**=**).
* In the forth part, I generate the **LLVM** code segments corresponding to this line (the statement tree).

Last 3 parts are also have the capabilty of some error detection. (Details can be found below.)


###   SECTION 2.1: main.cpp (Part 1)

This Part is responsible for the followings:
1. Creating/storing some variables which is used in other Parts.
2. Reading the lines from the input file.
3. Executing other Parts.
4. Catching the exceptions thrown in other Parts.
5. If an error is found, printing error message to the standard output stream.
6. If there are no errors, producing the output file.
	
Since we do not need to create a file when there is an error, I store the **LLVM** code segments in 2 strings first.
If there are no errors, I print these strings to the output file.

I declare a string (`allocateText`) which stores the instructions of **LLVM** about allocating variables. I seperate allocate instrunctions from other
instructions, because this string is also used (in Part 3) for checking whether a variable is defined or not before it is used in an expression.

I declare a string (`instructionText`) which stores the other instructions of **LLVM** such as _arithmetic calculation_, _assignment_, _print_, etc.

When we look at the **LLVM** code structure, we see that **LLVM** produces temporary variables the name of which in the form of `%<integer>`.
Also, note that the integer part of the name of the temporary variables increases from the beginning of the program to the end.
To keep track of the `<integer>` part of the name of the temporary variables, I declare an int (`tempVarInt`).

I declare an int (`lineNumber`) to keep track of the number of the line on which there exist an error.

Using a simple `WHILE loop`, `main()` reads the input file line by line and at each iteration (at each line) does the followings:

1. Creates a vector of strings which will contain the tokens of the line.
2. Calls `tokenize` function.
3. If catches an exception from `tokenize`, prints an error message and terminates the program.
4. Else, creates a `Node` pointer which will be the root of the statement tree that will be constructed.
5. Calls `checkErrorsAndGenerateTree` function.
6. If catches an exception from `checkErrorsAndGenerateTree`, prints an error message and terminates the program.
7. Else, calls `generateLLVMCode` function.

After `WHILE loop`, `main()` creates the output file and writes the whole **LLVM** codes in it.


### SECTION 2.2: Tokenizer.cpp (Part 2)

This Part is responsible for the followings:
1. Tokenizing a line of the input file and storing the tokens in a vector of string (`tokenVector`) which is created in `main()`.
2. Detecting invalid character error.
3. Detecting invalid variable name error.

`tokenize()` creates a string iterator and using a `WHILE loop` processes the line character by character from left to right.
There are 5 types of characters the iterator can find in the line.

* The first group consists of **+**, **-**, __*__, **/**, **=**, **(** and **)** characters. When one of these is found, it is inserted into `tokenVector` and 
next iteration of `WHILE` starts.

* The second group consists of decimal digit characters. When a digit is found, it means that there is either a `<number> token` or an _invalid variable name error_
in the line. A temporary string is created and the digit is added to the temporary string. Then, the iterator keeps moving as long as the characters
it sees are digits and those characters are added to the temporary string. After this point comes either (a) or (b):
  * (a) If the iterator comes to a character other than a letter (or the end of the line) after some sequence of digits, the temporary string
(which becomes a `<number> token`) is added to `tokenVector` and the next iteration of `WHILE` starts.
  * (b) If the iterator comes to a letter after some sequence of digits, it means that there is an _invalid variable name error_ such as _123abc_, _123ab45cd_, _12ab12_, etc.
In this case, the iterator keeps moving (for error message details) as long as the characters it sees are digits or letters and those characters are added
to the temporary string. When the iterator finally comes to a character other than a digit or a letter (or the end of the line), an exception is thrown with
the temporary string which is caught in `main()`.

* The third group consists of English letter characters. When a letter is found, it means that there is `<variable> token` in the line. A temporary string is created
and the letter is added to the temporary string. Then, the iterator keeps moving as long as the characters it sees are letters or digits and those characters are
added to the temporary string. When the iterator finally comes to a character other than a digit or a letter (or the end of the line), the temporary string
(which becomes a `<variable> token`) is added to `tokenVector` and the next iteration of `WHILE` starts.

* The forth group consist of _ignored characters_. Ignored characters are the characters the ASCII number of which is in the interval [0,32]. This ASCII interval contains
the space characters such as ` `, `\n`, `\r`, etc. When the iterator comes to such a character, it just moves to the next character
and the next iteration of `WHILE` starts.

* The last group consists of all the characters that are not belong to first 4 groups above. When such a character is found, an exception is thrown with
the string of "invalid character <character>" which is caught in `main()`.


### SECTION 2.3: ErrorCheckerAndTreeGenerator.cpp (Part 3)

This Part is responsible for the followings:
1. Detecting various syntax errors in `tokenVector` (which is created in `main()` and updated in `Part 2`).
2. Constructing a binary statement tree. A binary statement tree is a binary expression tree such that in addition to 4 binary
	operators (**+**, **-**, __*__, **/**) it can also contain the assignment operator (**=**).

In this Part, I use the idea of _recursive descent parser_. I implement a modified version of the parser found in
[here](https://en.wikipedia.org/wiki/Recursive_descent_parser).

First of all, I convert the grammar given in Section 1 (Problem Description) from BNF to EBNF as follows:
```
<statement>   =>  [ 'identifier' '=' ] <expression>
<expression>  =>  <term> { ('+' | '-') <term> }
<term>        =>  <factor> { ('*' | '/') <factor> }
<factor>      =>  'identifier'  |  'number'  |  '(' <expr> ')'
```

Terminals are represented in `'` and non-terminals are represented in `<` and `>`.
_number_ is an integer.
_identifier_ name can contain only letters and/or digits. Also, it cannot begin with a digit.

I implement mutually recursive functions for each of the 4 rules of the grammar (in EBNF) above. I also implement the functions `identifier()` and `number()`
for checking whether a token is an identifier or an integer, respectively.

`checkErrorsAndGenerateTree()` gets `tokenVector` which is created in Part 1&2 and insert the string of `!` to mark the end of the vector. Then, using a
vector iterator and parser functions processes it. The iterator is global with respect to the functions in this Part. The parser functions work coherently
and try to see whether the token to which the iterator points is "expected" according to the grammar (in EBNF) or not. If there are no problems with the token,
the iterator moves to the next token and the processes are repeated. Whenever a syntax error is detected, an exception is thrown with the string of some
details which is caught in `main()`.

Also, while checking syntax errors the parser functions construct a binary statement tree. A binary statement tree is a binary expression tree such that
in addition to 4 binary operators (**+**, **-**, __*__, **/**) it can also contain the assignment operator (**=**). I use a stack of `Node` pointers which is global with
respect to the functions in this Part to construct the tree. When the parser functions encounters an `identifier` or a `number` in `tokenVector`, 
a new Node (with the value of `identifier` or `number` and the children of `nullptr`) is created and the adress of this node is pushed to the stack.
When the parser functions encounters an <operator> (**+**, **-**, __*__, **/**) 2 elements of the stack are popped, a new `Node` (with the value of <operator> and
the children of popped elements) is created and the address of this node is pushed to the stack.

When the recursive parser functions are done (and no errors are found), the size of the stack can be either 1 or 2.

If the size is 1, it means that the statement in `tokenVector` was just an expression and he stack now contains the root of the expression (statement) tree. 
At this point, `checkErrorsAndGenerateTree()` pops the stack and assigns the element to `root` which is created in `main()`.

If the size is 2, it means that the statement in `tokenVector` was an assignment. In this case, the top element of the stack is the root of the
expression-part tree and the next element of the stack is just a `Node` which is the variable.
At this point, `checkErrorsAndGenerateTree()` pops the 2 element of the stack, creates a new `Node` (with the value of **=** and the children of popped elements),
and assigns this `Node` to `root` which is created in `main()`.


### SECTION 2.4: LLVMCodeGenerator.cpp (Part 4)

This Part is responsible for the followings:
1. Generating **LLVM** code segment corresponding to a binary statement tree. A binary statement tree is a binary expression tree such that in addition to 4 binary
	operators (**+**, **-**, __*__, **/**) it can also contain the assignment operator (**=**).
2. Detecting _undefined variable_ error.

The binary statement tree which is constructred in Part 3 has the following properties:
1. Every nodes have exactly 2 children except leaf nodes.
2. Every nodes other than leaves contain a binary operator (**+**, **-**, __*__, **/**).
3. The root of the tree can contain **=** in addition to 4 binary operators above.
4. The order of operations (arithmetic operations or assignment) are from bottom level of the tree to top level.

Considering these properties, I implement a recursive function (`generateCodeRecursively()`) which process the tree in post order manner and instead of 
evaluating the results of expressions and/or assignments, it generates **LLVM** code corresponding to those operations step by step. It uses `tempVarInt` which is in
`main()` for generating temporary variable names (e.g. %4) and stores generated code segments in `alllocateText` and `instructionText' which are in also `main()`.

The string `allocateText` in `main()` contains something like these:
`%var1 = alloca i32\n%var2 = alloca i32\n%var3 = alloca i32\n ...`

So, if a variable is defined, its name (such as _var1_, _var2_, etc.) appears in `allocateText`. Hence, if the name of a variable is found in `allocateText`,
it means that that variable is defined before. If not found, it means that that variable isn't defined.

When `generateCodeRecursively()` encounters a `Node` with the value **=**, it searches for the value of its left child (which is a variable name) in `allocateText`, and
if it is not found, it adds "%<value_of_left_child> = alloca i32\n" to `allocateText`.

When `generateCodeRecursively()` encounters a `Node` with the value of a variable name, it searches for that name in `allocateText` and if it is not found, an exception
is thrown with the string "undefined variable <variable>" which is caught in `main()`.

There are 3 possible types of string that can be returned from the last call of `generateCodeRecursively()`. These are:
**=** , **<number>** or **<LLVMTemporaryVariableName>** (which is in the form of %<integer>)
If the return value is **=**, it means that the statement was an assignment. In this case, nothing more needed to be done.
If the return value is **<number>** or **<LLVMTemporaryVariableName>**, it means that the statement was an expression and the value of return is the result of this expression
and it needs to be printed.

`generateLLVMCode()` gets the return value of the last call of `generateCodeRecursively()` and if it value is not **=**, it adds the neccessary **LLVM** instruction for printing
to `instructionText` which is in `main()`.

Also note that, in **LLVM** code, after print instruction the integer part of the next temporary variable should be incremented by **2** instead of **1**. `generateCodeRecursively()` increments it by 1 before it terminates. So, `generateLLVMCode()` increments it one more time before termination.



## SECTION 3: PROGRAM STRUCTURE

Node.h
*	struct Node	
*	Node()	
*	~Node()



Node.cpp
*	Node()
*	~Node()



Tokenizer.h
*	void tokenize(string &line, vector<string> &tokenVector)



Tokenizer.cpp
*	bool isDigit(char &c)	
*	bool isLetter(char &c)	
*	bool isIgnored(char &c)	
*	void tokenize(string &line, vector<string> &tokenVector)



ErrorCheckerAndTreeGenerator.h
*	void checkErrorsAndGenerateTree(vector<string> &tokenVector, Node *&root)



ErrorCheckerAndTreeGenerator.cpp
*	void checkErrorsAndGenerateTree(vector<string> &tokenVector, Node *&root)
*	void statement()
*	bool expression()
*	bool term()
*	bool factor()
*	bool identifier()
	bool number()



LLVMCodeGenerator.h
*	void generateLLVMCode(Node *&root, string &allocateText, string &instructionText, int &tempVarInt)



LLVMCodeGenerator.cpp
*	string generateCodeRecursively(Node *&node)
*	void generateLLVMCode(Node *&root, string &allocateText, string &instructionText, int &tempVarInt)



main.cpp
*	int main(int argc , char* argv[])
