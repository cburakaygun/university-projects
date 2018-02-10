## SECTION 1: PROBLEM DESCRIPTION

Write a Python program that can be invoked with the following options and
arguments:

`duplicates [-c <command> | -p] [-f | -d] ["..."] [<dir1> <dir2> ...]`
		
The `duplicates` program will traverse the directories and look for files or
directories that are exact duplicates of each other. It will then carry out an action
on the files or directories in question. The explanations of the options and
arguments are as follows:

`[-c <command> | -p]`

If `-c` is given, a command will be executed on the files/directories which are duplicates.
If `-p` is given , the paths of duplicates will be printed (a new line should be printed
between the sets of duplicates). Moreover, the paths of the empty directories should not be printed.
The default action is print.


`[-f | -d]`

If `-f` is given, the program will look for duplicate files.
If `-d` is given, the program will look for duplicate directories.
The default option is files.


`["..."]`

If given, the program will look for only the files or directories the name of which
matches the Python pattern given in “...“.
The default is all files or all directories.


`[<dir1> <dir2> ...]`

The list of directories to traverse (note that the directories will be traversed recursively,
i.e. directories and their subdirectories and their subdirectories etc. etc.).
The default is current directory.

Also, we can assume that directory hierarchy forms a tree.


## SECTION 2: PROBLEM SOLUTION (USING PYTHON3)

### SECTION 2.1: Locating the Duplicates

Locating duplicate FILES is easy. We just take `sha256` hash values of files and compare these hashes.
If the hash values of two files are the same, it is reasonable to assume that those files have the exact same content.

Locating duplicate DIRECTORIES is a bit more difficult than duplicate files. We **cannot** take `sha256` hash value
of a directory directly. When applied directly to a directory path, `sha256` gives the same hash value for all directories.

Here is a technique for calculating hash value of a directory:
* First, we take `sha256` hash values of the contents of the directory separately.
* Then, we concatenate those hash values in a string and take `sha256` hash value of that string.

Here, we need to be careful about something: The order in which hash values of the contents are concatenated to the string
changes the hash value of that string. For example, suppose **A**, **B** and **C** are 3 different hash values. `sha256` value of **ABC** and `sha256`
value of **CBA** will be different from each other.
So, our technique may produce different hash values for directories that have the same content in different order. To solve this problem,
I first store the hash values of the contents of the directory in a list. Then, I sort the list alphabetically. Then, I concatenate the
elements of sorted list in a string in that sorted order. Finally, I take `sha256` hash value of that string.



### SECTION 2.2: Implementing the Program

I used;
* `sys`			module to get the list of arguments given in the command-line
* `os`			module for operations which are related to file/directory paths
* `hashlib`		module to calculate `sha256` hash values
* `re`			module to check pattern given in ["..."] argument
* `subprocess`	module to execute the commands given in [-c <command>] argument


First of all, our program needs to process the options and arguments given in the command-line because
these options/arguments determine the way our program works.
For processing command-line options/arguments, we can use present modules such as `argparse`, but I choose to
write my own function to do this work to gain some flexibility.

I define 6 global variables to represent the command-line options/arguments in the program. They are as follows:
* `ARG_CMD`				: A string to store the value that is given after `-c` option in the command-line. Initial value is the empty string ("").
* `OPTION_P`			: A boolean to indicate whether `-p` option is given in the command-line or not. Initial value is `False`.
* `OPTION_F`			: A boolean to indicate whether `-f` option is given in the command-line or not. Initial value is `False`.
* `OPTION_D`			: A boolean to indicate whether `-d` option is given in the command-line or not. Initial value is `False`.
* `PATTERN`				: A string to store the value that is given between two `"`s (as pattern) in the command-line. Initial value is the empty string ("").
* `DIRS_TO_TRAVERSE`	: A list to store the directory paths that are given in the command-line. Since the number of directory paths that will be given in the command-line is not fixed, a list should be used. Initial value is the empty list ([]).


Since we locate duplicate files/directories using their hash values, we need to store the hash values and the corresponding paths
and also we need to map the hash values to its corresponding paths. For this, we can use the dictionary data stcructure 
(which contains `<key>:<value>` pairs) in which a hash value becomes the key and a list of paths becomes the value.
So, our dictionary is in the form of `{"hash1":['path1a' , 'path1b' , ...] , "hash2":['path2a' , 'path2b' , ...] , ...}`
Since I will use it frequently, I define a global dictionary (`HASH_MAP`).


I define 8 functions (including main) and distribute the tasks of the program among these functions. They are explained in the following sections:



#### SECTION 2.2.1: parse_args()

This function is responsible for the followings:
1. Processing the options/arguments given in the command-line and updating the global variables defined for the command-line options/arguments accordingly.
2. Detecting option/argument errors and in case of an error, printing an error message and terminating the program.
	
This function traverses the list `sys.argv[1:]` (Remember that `sys.argv[0]` stores the program name and it is not an argument.) using a `FOR LOOP`. Inside the `FOR
LOOP`, It tries to recognize given options/arguments using multiple `IF STATEMENT`s and it updates the global variables defined for the command-line options/arguments.
If no pattern is given in the command-line, this functions assigns the value __.*__ (which matches all names) to `PATTERN`.
If no directory path is given in the command-line, this function appends the path of the current working directory (which is the default value) to `DIRS_TO_TRAVERSE`.

The errors this function can detect are as follows:
1. Mutually exclusive options are given together. (`-c` and `-p` , `-f` and `-d` are considered as mutually exclusive.)
2. A command does not immediately follow the option `-c`.
3. More than one pattern are given.


#### SECTION 2.2.2: sha256_file(file_path)

This function is responsible for the followings:
1. Returning `sha256` hash value (in hex format) of the file given in its argument (`file_path`).
	
Calling the function `subprocess.check_output(cmd)`, this function executes Linux command `shasum -a 256 <file_path>` and gets its output in the form of
`b'<hash_value_in_hex> <path_of_the_file>\n'`. Then, the function decodes this output into the string `<hash_value_in_hex> <path_of_the_file>`. Finally,
using `split()` function gets a list from that string in the form of `['<hash_value_in_hex>' , '<path_of_the_file>']` and returns **0th** index of that list
which is the string of a hash value in hex format.


#### SECTION 2.2.3: is_pattern(str)

This function is responsible for the followings:
1. Checking whether the string in its argument (`str`) is a pattern (regex) or not and returning `True` or `False` accordingly.
	
This function tries to determine if `str` is a pattern or not using a single `IF STATEMENT`. For this program, strings given in the command-line
which starts and ends with a `"` are considered as a pattern. So, the minimum length of a pattern string is two, which is the length of the string `""`.


#### SECTION 2.2.4: process_files(dir_path)

This function is responsible for the followings:
1. Traversing the directory given in its argument recursively (i.e. `dir_path` and all the subdirectories of `dir_path`), calculating `sha256` hash values of the files
	   which are contained in those directories and the name of which matches `PATTERN` and adding hash values of those files and corresponding paths to the
	   dictionary `HASH_MAP`.
	
This function first creates a list (`dir_list`) that contains directory paths only. When created, the only element of the list is `dir_path`.
Then, using a `WHILE LOOP`, the function gets elements (`dir_path_name`) of the list. At each `WHILE` iteration, it processes the contents (`cur_dir_list`)
of `dir_path_name` using a `FOR LOOP`. For each content;
if the content is a directory, its path is added to 'dir_list'. If the content is a file, there are 2 situations:
1. If the hash value of the file does not exist in `HASH_MAP`, 
	the function adds the hash value as a key and the list of the file path as the corresponding value in `HASH_MAP`.
2. If the hash value of the file exist in `HASH_MAP`,
	the function adds the path of the file to the value (which is a list of file paths) of the corresponding key (hash value) in `HASH_MAP`.
	
When this function finishes its execution, `HASH_MAP` will contain `<key>:<value>` pairs, where `<key>`s are the hash values in hex format and
`<value>`s are the list of file paths whose hash value is in the key.


#### SECTION 2.2.5: process_dirs(dir_path)

This function is responsible for the followings:
1. Traversing the directory given in its argument recursively (i.e. `dir_path` and all the subdirectories of `dir_path`), calculating `sha256` hash values of the directories
	   the name of which matches `PATTERN` and adding hash values of those directories and corresponding paths to the dictionary `HASH_MAP`.
2. Returning the string of hash value of the directory `dir_path`.
	
This function first creates a list (`dir_content_hash_list`) that contains strings of hash value. It is initially empty list.
The function processes the contents (`cur_dir_list`) of `dir_path_name` using a `FOR LOOP`. For each content;
if the content is a file, its hash value is calculated using `sha256_file` function and added to the list `dir_content_hash_list`.
If the content is a directory, its hash value is calculated using `process_dirs` function and added to the list `dir_content_hash_list`.

Then, the function sorts the list 'dir_content_hash_list' alphabetically, concatenates the elements of the sorted list in a string (`cur_dir_hash`) in that sorted order and takes `sha256` hash value of that string. This last hash value is the hash value of the current directory (`dir_path`).

Then:
* If the hash value of the directory does not exist in `HASH_MAP`,
	the function adds the hash value as a key and the list of the directory path as the corresponding value in `HASH_MAP`.
* If the hash value of the directory exist in `HASH_MAP`,
	the function adds the path of the directory to the value (which is a list of directory paths) of the corresponding key (hash value) in `HASH_MAP`.

Finally, the function returns the hash value of the directory (`dir_path`).

When this function finishes its execution, `HASH_MAP` will contain `<key>:<value>` pairs, where `<key>`s are the hash values in hex format and
`<value>`s are the list of directory paths whose hash value is in the key.



#### SECTION 2.2.6: action_print()

This function is responsible for the followings:
1. Printing the paths of duplicate files/directories contained in the dictionary `HASH_MAP`.
	
Remember that `HASH_MAP` is in the form of `{"hash1":['path1a' , 'path1b' , ...] , "hash2":['path2a' , 'path2b' , ...] , ...}` which states that
the files/directories found in the paths 'path1a' , 'path1b' , ... are the same; the files/directories found in the paths 'path2a' , 'path2b' , ... are the same; and so on.

This function traverses the keys (hash values) of the dictionary `HASH_MAP` using a `FOR LOOP`. For each key (hash) value, it gets a list of paths.
If that list contains only one element, it means that element is not a duplicate. In this case, nothing is done and the function continues with the next key (hash) value.
If that list contains more than one element, the function traverses these elements using another FOR LOOP and prints the elements (paths) at a line, separated by white space. (' ').
After a group of paths is printed at a line, the function prints a new-line and continues with the next key (hash) value of the dictionary `HASH_MAP`.



#### SECTION 2.2.7: action_command(cmd)

This function is responsible for the followings:
1. Executing the command given in its argument (`cmd`) on each of the duplicate files/directories contained in the dictionary `HASH_MAP`.

First, the function creates a list (`args_of_cmd`) using built-in `split` function on the `cmd`. This list will be given to `run` function of `subprocess` module.
Then, the function adss a empty string to the end of `args_of_cmd`. This empty string will be replaced with a path on which the command will be executed.
	
Remember that `HASH_MAP` is in the form of `{"hash1":['path1a' , 'path1b' , ...] , "hash2":['path2a' , 'path2b' , ...] , ...}` which states that
the files/directories found in the paths 'path1a' , 'path1b' , ... are the same; the files/directories found in the paths 'path2a' , 'path2b' , ... are the same; and so on.

The function traverses the keys (hash values) of the dictionary `HASH_MAP` using a `FOR LOOP`. For each key (hash) value, it gets a list of paths.
If that list contains only one element, it means that element is not a duplicate. In this case, nothing is done and the function continues with the next key (hash) value.
If that list contains more than one element, the function traverses these elements using another `FOR LOOP`. At each `FOR` iteration,
the function replaces the last the element of `args_of_cmd` with the path from the list and calls `subprocess.run(args_of_cmd)` to execute the commmand `cmd` on that path.

#### AN EXAMPLE FOR action_command(cmd)

Suppose `cmd` is given as `shasum -a 256` and `HASH_MAP` is like:

`{'2ab1235fas':['path/to/file1' , 'path/to/file2']}`


First, `args_of_cmd` is created with `cmd.split()`:
`['shasum' , '-a' , '256']`


Then, the empty string is appended to `args_of_cmd`:
`['shasum' , '-a' , '256' , '']`


Then, in the second `FOR LOOP`, `args_of_cmd` becomes: 
`['shasum' , '-a' , '256' , 'path/to/file1']`


Then, in the second `FOR LOOP`, `subprocess.run(['shasum' , '-a' , '256' , 'path/to/file1'])` is called. (It means the command `shasum -a 256 path/to/file1` is executed.)


Then, in the second `FOR LOOP`, `args_of_cmd` becomes: 
`['shasum' , '-a' , '256' , 'path/to/file2']`


Then, in the second `FOR LOOP`, `subprocess.run(['shasum' , '-a' , '256' , 'path/to/file2'])` is called. (It means the command `shasum -a 256 path/to/file2` is executed.)



#### SECTION 2.2.8: main()

This function is responsible for the followings:
1. Calling some of the functions described above according to the given options/arguments in the command-line.

First, the function calls "parse_args()" which parses the arguments given in the command-line.

Then, if the option `-d` was given in the command-line (if the `OPTION_D == True`),
using a `FOR LOOP` it gets the elements of `DIRS_TO_TRAVERSE` and calls `process_dirs(dir_path)` with those elements.
If the option `-d` was NOT given in the command-line (if the `OPTION_D == False`), it means that the option `-f` was given or
neither `-f` nor `-d` was given in the command-line. In both cases, the function gets the elements of `DIRS_TO_TRAVERSE` using a `FOR LOOP` and
calls `process_files(dir_path)` with those elements. (Remember that the default is processing the files.)


Then, if potential duplicate files/directories were found (if `HASH_MAP` is not empty);
* and the option `-c` was given in the command-line (if the `ARG_CMD` is not empty), the function calls `action_command(ARG_CMD)`.
	
* and the option `-c` was NOT given in the command-line (if the `ARG_CMD` is empty), it means that the option `-p` was given or
	neither `-p` nor `-c` was given in the command-line. In both cases, the function calls `action_print()`. (Remember that the default action is printing.)



## SECTION 3: PROGRAM STRUCTURE

duplicates.py
* import sys , os , hashlib , re , subprocess
	
* def parse_args()
	* Returns:		None
	* Updates Global Variables:  ARG_CMD , OPTION_P , OPTION_F , OPTION_D , PATTERN , DIRS_TO_TRAVERSE

* def sha256_file(file_path)
	* Returns:		a string of sha256 hash value in hex format
	* Uses/Updates Global Variables:  None
		
* def is_pattern(str)
	* Returns:		if 'str' is a pattern, True. Otherwise, False
	* Uses/Updates Global Variables:  None

* def process_files(dir_path)
	* Returns:		None
	* Uses Global Variables:      PATTERN
	* Updates Global Variables:   HASH_MAP

* def process_dirs(dir_path)
	* Returns:		a string of sha256 hash value of the directory ('dir_path') in hex format
	* Uses Global Variables:      PATTERN
	* Updates Global Variables:   HASH_MAP
		
* def action_print()
	* Returns:		None
	* Uses Global Variables:      HASH_MAP
	* Updates Global Variables:   None

* def action_command(cmd)
	* Returns:		None
	* Uses Global Variables:      HASH_MAP
	* Updates Global Variables:   None

* def main()
	* Returns		None
	* Uses Global Variables:      OPTION_D , DIRS_TO_TRAVERSE , HASH_MAP , ARG_CMD
	* Updates Global Variables:   None
