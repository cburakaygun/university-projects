"""
This script is used to process search queries.
"""


import helper
import json
import os
import re
import sys


INDEX_DICT = dict()	  # Term-document inverted index
BIGRAM_DICT = dict()  # Bigram-term inverted index


def print_usage():
	"""
	Prints the manual for process.py
	"""
	print("""
	USAGE:
	python3 process.py <query_type> <query>
	
	<query_type>s:
	1  :  AND of single-words
	2  :  OR  of single-words
	3  :  single-word with one * (wildcard)
	
	EXAMPLES:
	python3 process.py 1 "car AND tree"
	python3 process.py 2 "car OR tree OR sun"
	python3 process.py 3 a*
	python3 process.py 3 comp*ter
	python3 process.py 3 *z
	""")


def handle_query_type1(query):
	"""
	Handles type-1 (i.e. AND of single-words) queries. Assumes the `query` contains at least one word other than AND.
	:param query: A query of type-1
	:return: A sorted list of document ids of documents that match the given `query` (list of int)
	"""
	terms = [x.strip().lower() for x in query.split(" AND ")]

	result_set = set(INDEX_DICT.get(terms.pop(), []))
	for term in terms:
		result_set &= set(INDEX_DICT.get(term, []))

	return sorted(result_set)


def handle_query_type2(query):
	"""
	Handles type-2 (i.e. OR of single-words) queries. Assumes the `query` contains at least one word other than OR.
	:param query: A query of type-2
	:return: A sorted list of document ids of documents that match the given `query` (list of int)
	"""
	terms = [x.strip().lower() for x in query.split(" OR ")]

	result_set = set()
	for term in terms:
		result_set |= set(INDEX_DICT.get(term, []))

	return sorted(result_set)


def handle_query_type3(query):
	"""
	Handles type-3 (i.e. single-word with *) queries. Assumes the `query` contains exactly one * (wildcard) in it.
	:param query: A query of type-3
	:return: A sorted list of document ids of documents that match the given `query` (list of int)
	"""
	(before_wildcard, after_wildcard) = query.strip().lower().split('*')

	# Creates bigrams for search query.
	bigram_set = set(helper.create_bigrams_for('$' + before_wildcard))
	bigram_set.update(helper.create_bigrams_for(after_wildcard + '$'))

	if len(bigram_set) == 0:
		return []

	# Gets terms from bigrams.
	term_set = set(BIGRAM_DICT.get(bigram_set.pop(), []))
	for bgram in bigram_set:
		term_set &= set(BIGRAM_DICT.get(bgram, []))

	# Post-filters terms for false positives.
	terms = [term for term in term_set if re.fullmatch(before_wildcard + '.*' + after_wildcard, term)]

	return handle_query_type2(' OR '.join(terms))


def read_index_files():
	"""
	Reads 'index.json' and 'bigrams.json' into `INDEX_DICT` and `BIGRAM_DICT`, respectively.
	:return: True, if both files exist. False, otherwise.
	"""
	global INDEX_DICT, BIGRAM_DICT

	if os.path.isfile(helper.INDEX_FILE_PATH) and os.path.isfile(helper.BIGRAM_INDEX_FILE_PATH):
		with open(helper.INDEX_FILE_PATH) as f:
			INDEX_DICT = json.load(f)

		with open(helper.BIGRAM_INDEX_FILE_PATH) as f:
			BIGRAM_DICT = json.load(f)

		return True
	else:
		return False


# Script starts here.
if __name__ == "__main__":
	if len(sys.argv) < 3:
		print("Wrong number of arguments.")
		print_usage()
	else:
		query_type = sys.argv[1]
		query = sys.argv[2]

		if query_type not in ['1', '2', '3']:
			print("Invalid query type.")
			print_usage()
		else:
			print("Processing the query: {0}\n".format(query))

			if read_index_files():
				result = []

				if query_type == '1':
					result = handle_query_type1(query)
				elif query_type == '2':
					result = handle_query_type2(query)
				elif query_type == '3':
					if query.count('*') != 1:
						print("Type-3 query word must have exactly one wildcard (*) character in it.")
					else:
						result = handle_query_type3(query)

				if len(result) == 0:
					print("No matching documents are found.")
				else:
					print(result)
			else:
				print("Index files (index.json and/or bigrams.json) are missing. Try running pre-process.py.")
