"""
This script is used to pre-process Reuters-21578 data set
to create inverted indexes ('index.json' and 'bigrams.json' files).
"""


from datetime import datetime
import helper
import json
import os
import re


# This regex pattern matches <REUTERS>... block and produces 2 groups for a match:
# First group captures the NEWID and the second one captures <TEXT>...</TEXT> block.
REUTERS_RGX = r'<REUTERS.+?NEWID="(\d+)".+?(<TEXT.+?</TEXT>)'

TITLE_RGX = r'<TITLE.*?>(.+)</TITLE'  # Regex pattern used to extract the title from <TEXT>...</TEXT> block.
BODY_RGX = r'<BODY.*?>(.+)</BODY'  # Regex pattern used to extract the body from <TEXT>...</TEXT> block.

PUNCTUATION_RGX = ""  # A regex pattern which will be used to replace some characters with space while tokenizing.
STOPWORDS = []  # A list that contains the stopwords to be removed.

INDEX_DICT = dict()  # Term-document inverted index.
TEMP_INDEX_DICT = dict()  # Temporary term-document inverted index that is used to build the actual one.


# Number of all tokens before the stopwords are removed.
TOKENS_BEFORE_STOPWORDS = -1

# Number of all tokens after the stopwords are removed.
TOKENS_AFTER_STOPWORDS = -1

# Number of all terms (unique tokens) before the stopwords are removed and case-folding is applied.
TERMS_BEFORE_STOPWORDS_AND_CASEFOLDING = -1

# Number of all terms (unique tokens) after the stopwords are removed and case-folding is applied.
TERMS_AFTER_STOPWORDS_AND_CASEFOLDING = -1


def tokenize(text):
	"""
	Tokenizes the parameter `text` by first replacing some punctuation characters with space
	and then splitting it from space.
	:param text: A string to be tokenized
	:return: A list of tokens (list of str)
	"""
	return re.sub(PUNCTUATION_RGX, " ", text).split()


def create_temp_index_dict():
	"""
	Creates a temporary term-document inverted index (TEMP_INDEX_DICT).
	Also, calculates the total number of tokens before and after the stopwords are removed.
	TEMP_INDEX_DICT contains the terms before the stopwords are removed and case-folding is applied.
	"""
	global TOKENS_BEFORE_STOPWORDS, TOKENS_AFTER_STOPWORDS

	total_stopwords = 0  # Number of total stopwords encountered in the data set.

	# Names of all the data set files.
	reut2_filenames = [x for x in os.listdir(helper.REUTERS_DIR_PATH) if x.startswith("reut2")]

	for reut2_filename in sorted(reut2_filenames):
		with open(os.path.join(helper.REUTERS_DIR_PATH, reut2_filename), "rb") as reut2_file:
			content = reut2_file.read().decode("latin-1")

			for article_info in re.findall(REUTERS_RGX, content, re.DOTALL | re.IGNORECASE):
				(doc_id, text_tag) = article_info  # `doc_id` is 'NEWID' field and `text_tag` is <TEXT>...</TEXT> block.

				doc_id = int(doc_id)

				text = ""  # Actual text (title and body) of <TEXT>...</TEXT> block.
				title = re.search(TITLE_RGX, text_tag, re.DOTALL | re.IGNORECASE)
				if title:
					text += title.group(1)

				body = re.search(BODY_RGX, text_tag, re.DOTALL | re.IGNORECASE)
				if body:
					text += " " + body.group(1)

				tokens = tokenize(text)  # List of tokens produced from `text`.

				TOKENS_BEFORE_STOPWORDS += len(tokens)

				for token in tokens:
					if token in STOPWORDS:
						total_stopwords += 1

					if token not in TEMP_INDEX_DICT:
						TEMP_INDEX_DICT[token] = [doc_id]
					else:
						if doc_id not in TEMP_INDEX_DICT[token]:
							TEMP_INDEX_DICT[token].append(doc_id)  # Appends to list.

			print("\t{0} was processed.".format(reut2_file.name))

	TOKENS_AFTER_STOPWORDS = TOKENS_BEFORE_STOPWORDS - total_stopwords


def create_index_dict():
	"""
	Creates the actual term-document inverted index (INDEX_DICT) from the temporary one (TEMP_INDEX_DICT).
	Effectively, deletes the stopwords from the terms and applies case-folding (lowercase).
	"""
	for (term, doc_ids) in TEMP_INDEX_DICT.items():  # `doc_ids` is a list of int
		term_lowercase = term.lower()

		if term_lowercase not in STOPWORDS:
			if term_lowercase not in INDEX_DICT:
				INDEX_DICT[term_lowercase] = doc_ids
			else:
				INDEX_DICT[term_lowercase] = list(set(INDEX_DICT[term_lowercase] + doc_ids))  # Merges document ids.


def create_index_file(file_path, index_to_dump):
	"""
	Dumps the parameter `index_to_dump` to a json file given as `file_path`.
	This function is used to create 'index.json' and 'bigrams.json' files.
	:param file_path: The path of the file to be created.
	:param index_to_dump: A dictionary object to be dumped into a JSON file.
	"""
	# Sorts the document ids (for 'index.json') and terms (for 'bigrams.json').
	for val in index_to_dump.values():
		val.sort()

	with open(file_path, 'w') as f:
		json.dump(index_to_dump, f, sort_keys=True)
		print("[INFO]\t" + f.name + " was created.")


def create_bigrams_dict(term_list):
	"""
	Creates a bigram-term inverted index and returns it.
	:param term_list: List of tokens from which the bigram index will be constructed.
	:return: A dictionary object which maps bigrams to the list of terms { str: [str, str, ...] }
	"""
	bigrams_dict = dict()  # Bigram index

	for term in term_list:
		for bgram in helper.create_bigrams_for('$' + term + '$'):
			if bgram not in bigrams_dict:
				bigrams_dict[bgram] = [term]
			else:
				if term not in bigrams_dict[bgram]:
					bigrams_dict[bgram].append(term)  # Appends to list.

	return bigrams_dict


def print_20_most_freq_terms(terms_dict):
	"""
	Prints the most 20 frequent terms in the parameter `terms_dict`.
	:param terms_dict: A dictionary object which maps terms to list of document ids ( { str: [int, int, ...] } )
	"""
	# This is a dictionary object which maps frequencies to a list of terms ( { int: [str, str, ...] } )
	# For example, if `terms_dict` contains { 'a': [1, 3, 5], 'b': [2, 4], 'c': [2, 5, 10] },
	# then `freq_dict` will contain { 3: ['a', 'c'], 2: ['b'] }
	freq_dict = dict()

	# This loop constructs `freq_dict` iterating over the parameter `terms_dict`
	for term in terms_dict:
		term_freq = len(terms_dict[term])  # Term frequency

		if term_freq not in freq_dict:
			freq_dict[term_freq] = [term]
		else:
			freq_dict[term_freq].append(term)  # Appends to list.

	term_size = 0  # Number of terms printed so far

	print('| ', end='')
	# This loop prints the most 20 frequent terms.
	for freq in sorted(freq_dict, reverse=True):
		if term_size < 20:
			term_size += len(freq_dict[freq])
			for term in sorted(freq_dict[freq]):
				print("{0} ({1}) | ".format(term, freq), end='')
		else:
			break

	print()


def print_stats():
	"""
	Prints the statistics about the tokens/terms of the data set.
	"""
	global TOKENS_BEFORE_STOPWORDS, TOKENS_AFTER_STOPWORDS, \
		TERMS_BEFORE_STOPWORDS_AND_CASEFOLDING, TERMS_AFTER_STOPWORDS_AND_CASEFOLDING

	print("\n\n% % % % % % % % % % STATISTICS % % % % % % % % % %")

	print("NUMBER OF TOKENS BEFORE STOPWORDS REMOVAL\t: {0}".format(TOKENS_BEFORE_STOPWORDS))
	print("NUMBER OF TOKENS AFTER STOPWORDS REMOVAL\t: {0}\n".format(TOKENS_AFTER_STOPWORDS))

	print("NUMBER OF TERMS BEFORE STOPWORDS REMOVAL AND CASE-FOLDING\t: {0}".format(
		TERMS_BEFORE_STOPWORDS_AND_CASEFOLDING))
	print("NUMBER OF TERMS AFTER STOPWORDS REMOVAL AND CASE-FOLDING\t: {0}\n".format(
		TERMS_AFTER_STOPWORDS_AND_CASEFOLDING))

	print("20 MOST FREQUENT TERMS BEFORE STOPWORD REMOVAL AND CASE-FOLDING:")
	print_20_most_freq_terms(TEMP_INDEX_DICT)

	print("\n20 MOST FREQUENT TERMS AFTER STOPWORD REMOVAL AND CASE-FOLDING:")
	print_20_most_freq_terms(INDEX_DICT)


# Script starts here.
if __name__ == "__main__":
	# Constructs PUNCTUATION_RGX.
	with open(helper.PUNCTUATIONS_FILE_PATH) as punc_file:
		punc_chars = [punc_char.strip() for punc_char in punc_file.readlines() if len(punc_char.strip()) != 0]
		PUNCTUATION_RGX = '(' + '|'.join([re.escape(punc_char) for punc_char in punc_chars]) + ')'

	# Constructs STOPWORDS.
	with open(helper.STOPWORDS_FILE_PATH) as stopwords_file:
		STOPWORDS = [x.strip().lower() for x in stopwords_file.readlines()]

	begin_time = datetime.now()

	# Creates Output directory if not exists.
	if not os.path.isdir(helper.INDEX_FILES_DIR_PATH):
		os.makedirs(helper.INDEX_FILES_DIR_PATH)

	print("\n[INFO]\tCreating term-document inverted index...")

	create_temp_index_dict()  # Creates the temporary term-document inverted index (TEMP_INDEX_DICT).
	TERMS_BEFORE_STOPWORDS_AND_CASEFOLDING = len(TEMP_INDEX_DICT)

	create_index_dict()  # Creates the actual term-document inverted index (INDEX_DICT).
	TERMS_AFTER_STOPWORDS_AND_CASEFOLDING = len(INDEX_DICT)

	create_index_file(helper.INDEX_FILE_PATH, INDEX_DICT)  # Creates 'index.json'.

	print("\n[INFO]\tCreating bigram-term inverted index...")
	# Creates a bigram index for the terms of term-document inverted index (INDEX_DICT) and writes it to 'bigrams.json'.
	create_index_file(helper.BIGRAM_INDEX_FILE_PATH, create_bigrams_dict(INDEX_DICT.keys()))

	end_time = datetime.now()

	print_stats()  # Print some statistics about the tokens/terms of the data set.

	print("\nPRE-PROCESS TIME (SEC): {0:.2f}".format((end_time-begin_time).total_seconds()))
