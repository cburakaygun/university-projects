### ABOUT:
This program was implemented using only Python 3.6.7. No libraries other than the built-in ones were utilized.

This program consists of 3 Python files/scripts:

  - code/pre-process.py
  - code/process.py
  - code/helper.py

***

### HOW TO RUN:
(Assuming you are on the parent directory (say ParentCode) of 'code' directory...)

1) Run **pre-process.py** to pre-process the dataset and create inverted index files:

    COMMAND:    `python3 code/pre-process.py`

    When run, this script pre-processes all the data files in **reuters21578** directory and creates 2 JSON files in directory ParentCode/Output:
      - index.json    :   This file is the term-document inverted index
      - bigrams.json  :   This file is the bigram-term inverted index

    Also, it prints some statistics about the tokens/terms of the data set on the terminal.

    **NOTES:**

      - This script expects a directory in ParentCode named **reuters21578**. **ParentCode/reuters21578/** should contain the dataset files (*reut2-000.sgm*, *reut2-001.sgm*, etc.)
      - This script expects the files **punctuations.txt** and **stopwords.txt** to be in ParentCode.
      - The input/output file/directory names/paths can be modified in **code/helper.py** file.


2) After you pre-process the dataset files and produce the 2 JSON files mentioned at step (1), run **process.py** to search for a query:

    COMMAND:    `python3 code/process.py <query_type> <query>`

      <query_type>s:

      ```
      1  :  AND of single-words
      2  :  OR  of single-words
      3  :  single-word with one * (wildcard)
      ```
	
      **EXAMPLES:**
	
        python3 process.py 1 "car AND tree"
        python3 process.py 2 "car OR tree OR sun"
        python3 process.py 3 a*
        python3 process.py 3 comp*ter
        python3 process.py 3 *z

      This script searches for the provided `<query>` in **index.json** (and **bigrams.json** in case of type-3 query) and prints a sorted (in increasing order) list of document ids of the matching documents.
