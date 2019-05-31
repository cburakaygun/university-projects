from os.path import abspath, dirname, join

#
# This file contains some common variables/functions to be used in pre-process.py and process.py
#


# Absolute path of the parent directory of Code directory.
# helper.py, pre-process.py and process.py are assumed to be in Code directory.
parent_code_dir_path = dirname(dirname(abspath(__file__)))


# Input files/directories:

# The directory which stores Reuters-21578 data files.
REUTERS_DIR_PATH = join(parent_code_dir_path, "reuters21578/")

# Path of the file that contains the stopwords to be removed.
STOPWORDS_FILE_PATH = join(parent_code_dir_path, "stopwords.txt")

# Path of the file that contains the punctuations to be removed on tokenizing.
PUNCTUATIONS_FILE_PATH = join(parent_code_dir_path, "punctuations.txt")


# Output files/directories:

# The directory in which 'index.json' and 'bigrams.json' will be created.
INDEX_FILES_DIR_PATH = join(parent_code_dir_path, "Output/")

# Path of 'index.json' file.
INDEX_FILE_PATH = join(INDEX_FILES_DIR_PATH, 'index.json')

# Path of 'bigrams.json' file.
BIGRAM_INDEX_FILE_PATH = join(INDEX_FILES_DIR_PATH, 'bigrams.json')


def create_bigrams_for(token):
	"""
	A helper function to create bigrams of the parameter `token`.
	This function is used on pre-process.py to create bigram-term inverted index
	and on process.py for type-3 query processing.
	:param token: A string from which bigrams will be created
	:return: A list of bigrams (list of str)
	Example input => output: "cmpe" => ['cm', 'mp', 'pe']
	"""
	result = []

	for i in range(len(token) - 1):
		result.append(token[i:i+2])

	return result
