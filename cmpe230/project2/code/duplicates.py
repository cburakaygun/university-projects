# 2017
# CMPE230 HOMEWORK 2
# CEMAL BURAK AYGÜN
# 2014400072


import sys , os , hashlib , re , subprocess


HASH_MAP = {}	# Dictionary used to store hash values (key) of files/directories and corresponding file/directory paths (value).
				# Every key (hash value) maps to a list of file/directory paths.
				# In the form of {"hash1":['path1a' , 'path1b' , ...] , "hash2":['path2a' , 'path2b' , ...] , ...}


# VARIABLES FOR COMMAND-LINE ARGUMENTS
ARG_CMD = ""			# A string to store the value that is given after '-c' option in the command-line.
OPTION_P = False		# A boolean to indicate whether '-p' option is given in the command-line or not.
OPTION_F = False		# A boolean to indicate whether '-f' option is given in the command-line or not.
OPTION_D = False		# A boolean to indicate whether '-d' option is given in the command-line or not.
PATTERN = ""			# A string to store the value that is given between two "s (as pattern) in the command-line.
DIRS_TO_TRAVERSE = []	# A list to store the directory paths that are given in the command-line. Assumed that given directories are disjoint (non-intersecting).



# This function traverses the arguments given in the command-line and updates "VARIABLES FOR COMMAND-LINE ARGUMENTS" listed above accordingly.
# If it encounters a mistake related to the options/arguments, it prints an error message and terminates the program.
def parse_args(): 

	global ARG_CMD , OPTION_P , OPTION_F , OPTION_D , PATTERN , DIRS_TO_TRAVERSE

	itr = iter( sys.argv[1:] )		# An iterator for command-line arguments. Remember that sys.argv[0] stores the program name and it is not an argument.

	for token in itr:

		if token == "-c":		# If current option is '-c' ...
			
			if OPTION_P:		# ... and if the option '-p' was given before,
				print("ERROR:  -c AND -p ARE MUTUALLY EXCLUSIVE")		# prints an error message and
				exit(0)													# terminates the program.

			ARG_CMD = next(itr,None)	# ... stores the value of the next argument into 'ARG_CMD'. If the next argument does not exist, 'None' will be stored in 'ARG_CMD'.
			
			if ARG_CMD == None or ARG_CMD == "-p" or ARG_CMD == "-f" or ARG_CMD == "-d":	# If a command is missing after '-c' option,
				print("ERROR:  -c EXPECTS ONE ARGUMENT")		# prints an error message and
				exit(0)											# terminates the program.

		elif token == "-p":		# If current option is '-p' ...
			
			if ARG_CMD:			# ... and if the option '-c' was given before,
				print("ERROR:  -p AND -c ARE MUTUALLY EXCLUSIVE")		# prints an error message and
				exit(0)													# terminates the program.

			OPTION_P = True

		elif token == "-f":		# If current option is '-f' ...
			
			if OPTION_D:		# ... and if the option '-d' was given before,
				print("ERROR:  -f AND -d ARE MUTUALLY EXCLUSIVE")		# prints an error message and
				exit(0)													# terminates the program.

			OPTION_F = True

		elif token == "-d":		# If current option is '-d' ...
			
			if OPTION_F:		# ... and if the option '-f' was given before,
				print("ERROR:  -d AND -f ARE MUTUALLY EXCLUSIVE")		# prints an error message and
				exit(0)													# terminates the program.

			OPTION_D = True

		elif is_pattern(token):		# If current argument is a pattern ...
			
			if PATTERN:				# ... and if another pattern was given before,
				print("ERROR:  MORE THAN ONE PATTERN ARE NOT ALLOWED")		# prints an error message and
				exit(0)														# terminates the program.

			PATTERN = token[1:-1]	# Deletes "s or 's from the beginning and from the end of the argument (pattern) and stores it into 'PATTERN'.

		else:		# The arguments that do not fit any of the IF STATEMENTs above are assumed to be directory paths.
			DIRS_TO_TRAVERSE.append( os.path.abspath(token) )

	
	if not PATTERN:			# If no pattern is given in the command-line,
		PATTERN = r".*"		# stores the default value (which corresponds to all the file/directory names) into 'PATTERN'.

	if not DIRS_TO_TRAVERSE:	# If no directory path is given in the command-line,
		DIRS_TO_TRAVERSE.append( os.getcwd() )	 # stores the default value (which is the current directory path) into 'DIRS_TO_TRAVERSE'.



# This function takes a file path as its argument and returns the sha256 hash value of that file in hex format.
def sha256_file(file_path):

	sha256_cmd = ['shasum' , '-a' , '256' , file_path]	# Linux command for calculating 256 hash value of the file 'file_path' (shasum -a 256 <file_path>)
	
	output = subprocess.check_output(sha256_cmd)	# Output of the command 'sha25_cmd'

	return output.decode().split(maxsplit=1)[0]		# Extracts the hash value from 'output' and returns it as a string.



# This function takes a string as its argument and checks whether it is a pattern (regex) or not.
# For this program, strings whose first and last characters are " or ' considered as a pattern.
# If 'str' is a pattern, returns True. Else, returns False.
def is_pattern(str):

	if len(str) >= 2 and ((str[0] == '"' and str[-1] == '"') or (str[0] == "'" and str[-1] == "'")):
		return True
	
	else:
		return False



# This function takes a directory path as its argument and calculates the sha256 hash values (in hex format) of all the files
# (the files in 'dir_path' and all of the subdirectories) the name of which matches 'PATTERN'.
#
# When this function finishes its execution, 'HASH_MAP' will contain <key>:<value> pairs, where <key>s are the hash values in hex format and
# <value>s are the list of file paths whose hash value is in the key.
def process_files(dir_path):

	dir_list = [dir_path]	# A list which contains directory paths only

	while dir_list:

		dir_path_name = dir_list.pop(0)		# Pops the first element of 'dir_list'
		cur_dir_list = os.listdir(dir_path_name)	# A list of the contents of the directory ('dir_path_name')

		for fd_name in cur_dir_list:

			the_path = os.path.join(dir_path_name , fd_name)

			if os.path.isdir(the_path):
				dir_list.append(the_path)

			if os.path.isfile(the_path) and re.search(PATTERN , fd_name , re.DOTALL):		# If the name of the current file matches 'PATTERN'
				cur_file_hash = sha256_file(the_path)

				if not cur_file_hash in HASH_MAP:			# If the hash value (key) does not exist in 'HASH_MAP',
					HASH_MAP[cur_file_hash] = [the_path]	# adds the hash value as a key and the list of the file path as the corresponding value in 'HASH_MAP'.
				
				else:										# If the hash value (key) already exists in 'HASH_MAP',
					HASH_MAP[cur_file_hash].append(the_path)	# updates the value (which is a list of file paths) of the corresponding key (hash value).



# This function takes a directory path as its argument and calculates the sha256 hash values (in hex format) of all the directories ('dir_path' and all of the subdirectories)
# which are not empty and the name of which matches 'PATTERN'.
# Also, it returns the string of calculated hash value of the directory ('dir_path').
#
# When this function finishes its execution, 'HASH_MAP' will contain <key>:<value> pairs, where <key>s are the hash values in hex format and
# <value>s are the list of directory paths whose hash value is in the key.
#
# The hex value of a directory is calculated as follows:
# Firstly, the hash values of the contents of the directory is stored in a list ('dir_content_hash_list').
# Secondly, the list obtained in previous step is sorted alphabetically.
# Thirdly, the elements of sorted list is concatenated in a string ('cur_dir_hash') in that sorted order.
# Finally, sha256 hash value of the string in 'cur_dir_hash' is calculated in hex format.
def process_dirs(dir_path):

	dir_content_hash_list = []  		# A list which stores the hash values of the contents of the current directory ('dir_path')
	cur_dir_list = os.listdir(dir_path)		# List of the contents of the current directory ('dir_path')

	for fd_name in cur_dir_list:
		
		the_path = os.path.join(dir_path , fd_name)

		if os.path.isfile(the_path):
			dir_content_hash_list.append( sha256_file(the_path) )

		if os.path.isdir(the_path):
			dir_content_hash_list.append( process_dirs(the_path) )

	cur_dir_hash = ""		# Hash value of the current directory ('dir_path')
	for hsh in sorted( dir_content_hash_list ):
		cur_dir_hash += hsh
	cur_dir_hash = hashlib.sha256( cur_dir_hash.encode() ).hexdigest()

	if cur_dir_list and re.search(PATTERN , os.path.basename(dir_path) , re.DOTALL):	# If the current directory is not empty and the name of the current directory matches 'PATTERN'
		
		if not cur_dir_hash in HASH_MAP:			# If the hash value (key) does not exist in 'HASH_MAP',
			HASH_MAP[cur_dir_hash] = [dir_path]		# adds the hash value as a key and the list of the directory path as the corresponding value in 'HASH_MAP'.
		
		else:										# If the hash value (key) already exists in 'HASH_MAP',
			HASH_MAP[cur_dir_hash].append(dir_path)	# updates the value (which is a list of directoy paths) of the corresponding key (hash value).

	return cur_dir_hash		# Returns the string of hash value of the directory ('dir_path').



# This function traverses 'HASH_MAP' and prints the paths of the files/directories which are duplicates.
#
# Note that 'HASH_MAP' contains <key>:<value> pairs, where <key>s are hash values in hex format and
# <value>s are the list of file/directory paths whose hash value is in the key.
def action_print():
	
	for key in HASH_MAP:

		if len(HASH_MAP[key]) == 1:		# If the length of the list corresponding to a key is equal to 1, it means that only one file/directory
			continue					# is found with this key. So, it is not a duplicate. Hence, it continues to the next key in 'HASH_MAP'.

		for path in sorted(HASH_MAP[key]):	# Prints all the paths (sorted alphabetically) corresponding to the key (hash value).
			print(path, end="        ")			# Duplicate paths are printed such that they are seperated by white space (' ').

		print("\n")							# Prints two new-line (one of them is implicit in "print" function) for the next group of duplicate files/directories.



# This function takes a string of command as its argument and traversing 'HASH_MAP' executes this command on the files/directories which are duplicates.
#
# Note that 'HASH_MAP' contains <key>-<value> pairs, where <key>s are hash values in hex format and
# <value>s are the list of file/directory paths whose hash value is in the key.
def action_command(cmd):
	
	args_of_cmd = cmd.split()	# A list the elements of which are the words in 'cmd' (words are seperated by white spaces).
	args_of_cmd.append("")		# Appends an empty string to the end of the list 'args_of_cmd'.
								# This empty string will be replaced with file/directory path in the FOR LOOP below.

	for key in HASH_MAP:

		if len(HASH_MAP[key]) == 1:		# If the length of the list corresponding to a key is equal to 1, it means that only one file/directory
			continue					# is found with this key. So, it is not a duplicate. Hence, it continues to the next key in 'HASH_MAP'.

		for path in sorted(HASH_MAP[key]):
			args_of_cmd[-1] = path 			# Replaces the last element of 'arg_of_cmd' with current file/directory path.
			subprocess.run( args_of_cmd )	# Executes the command which is contained in the list 'args_of_cmd'.

		print()



# Main function of the program. It calls some functions according to the given arguments in the command-line.
def main():

	parse_args()		# Parses the arguments given in the command-line.

	if OPTION_D:		# If '-d' option is given in the command-line
		for path in DIRS_TO_TRAVERSE:
			process_dirs( path )		# Processes directories and updates 'HASH_MAP'

	else:				# If '-f' option is given or neither '-f' nor '-d' options are given in the command-line
		for path in DIRS_TO_TRAVERSE:
			process_files( path )		# Processes files and updates 'HASH_MAP'

	if HASH_MAP:		# If potential duplicate files/directories are found
		if ARG_CMD:		# If '-c ' option is given in the command-line
			action_command( ARG_CMD )		# Executes 'ARG_CMD' on duplicate files/directories.
		
		else:			# If '-p' option is given or neither '-p' nor '-c' options are given in the command-line
			action_print()		# Prints the paths of duplicate files/directories.



if __name__ == "__main__":
   main()