import math
import os
import re
import sys
import unicodedata


GROUP_ID = 3

PROGRAM_DIR_PATH = os.path.dirname(os.path.abspath(__file__))
DERLEM_PATH = os.path.join(PROGRAM_DIR_PATH, 'derlem.txt')


_, TEST_Q_PATH, TASK1_PRED_PATH, TASK2_PRED_PATH = sys.argv


#
# PART 1: RELATED PARAGRAPH
#


MIN_PARA_LEN = 8  # Paragraphs that have less than 8 words are ignored

TR_LETTERS_LOWER = r'a-zçğıöşü'
TR_LETTERS_UPPER = r'A-ZÇĞİÖŞÜ'
TR_LETTERS = r'{0}{1}'.format(TR_LETTERS_LOWER, TR_LETTERS_UPPER)

# Matches characters other than Turkish (and English) letters and digits.
TOKEN_SEPARATOR_RGX = r'[^{0}0-9]'.format(TR_LETTERS)

SENTENCE_SEPARATOR_RGX = r'(?<=[{0})][.:])\s+(?=["{1}0-9])'.format(TR_LETTERS_LOWER, TR_LETTERS_UPPER)

STOPWORDS = {
    'acaba', 'acep', 'açıkça', 'açıkçası', 'adamakıllı', 'adeta', 'ait', 'ama', 'amma', 'anca', 'ancak',
    'bana', 'bari', 'başkası', 'bazen', 'bazı', 'belki', 'ben', 'beri', 'beriki', 'bilcümle', 'bile', 'binaen',
    'binaenaleyh', 'bir', 'biraz', 'birazdan', 'birbiri', 'birçoğu', 'birden', 'birden', 'birdenbire', 'biri', 'birice',
    'birileri', 'birisi', 'birkaçı', 'birlikte', 'bitevi', 'biteviye', 'bittabi', 'biz', 'bizatihi', 'bizce',
    'bizcileyin', 'bizden', 'bizimki', 'bizzat', 'boşuna', 'böyle', 'böylece', 'böylecene', 'böylelikle', 'böylemesine',
    'böylesine', 'buna', 'bunda', 'bundan', 'bunlar', 'bunu', 'bunun', 'buracıkta', 'burada', 'buradan', 'burası',
    'büsbütün', 'çabuk', 'çabukça', 'çeşitli', 'çoğu', 'çoğun', 'çoğunca', 'çoğunlukla', 'çok', 'çokça', 'çokları',
    'çoklarınca', 'çokluk', 'çoklukla', 'cuk', 'cümlesi', 'çünkü', 'daha', 'dahi', 'dahil', 'dahilen', 'daima', 'değil',
    'değin', 'dek', 'demin', 'demincek', 'deminden', 'denli', 'derakap', 'derhal', 'derken', 'diğeri', 'diye', 'doğru',
    'dolayı', 'dolayısıyla', 'eğer', 'elbet', 'elbette', 'emme', 'enikonu', 'epey', 'epeyce', 'epeyi', 'esasen',
    'esnasında', 'etraflı', 'etraflıca', 'evleviyetle', 'evvel', 'evvela', 'evvelce', 'evvelden', 'evvelemirde',
    'evveli', 'fakat', 'filanca', 'gah', 'gayet', 'gayetle', 'gayri', 'gayrı', 'geçende', 'geçenlerde', 'gelgelelim',
    'gene', 'gerçi', 'gerek', 'gibi', 'gibilerden', 'gibisinden', 'gine', 'gırla', 'göre', 'hakeza', 'halbuki', 'halen',
    'halihazırda', 'haliyle', 'handiyse', 'hangisi', 'hani', 'hasebiyle', 'hasılı', 'hatta', 'hele', 'hem', 'hepsi',
    'hiçbiri', 'hoş', 'hulasaten', 'için', 'iken', 'ila', 'ile', 'ilen', 'illa', 'illaki', 'imdi', 'indinde', 'inen',
    'iş', 'ister', 'itibarıyla', 'iyice', 'iyicene', 'kaçı', 'kadar', 'kaffesi', 'kah', 'kala', 'karşın', 'kaynak',
    'kelli', 'kendi', 'keşke', 'kez', 'keza', 'kezalik', 'kim', 'kimi', 'kimisi', 'kimse', 'kimsecik', 'kimsecikler',
    'kısaca', 'külliyen', 'lakin', 'leh', 'lütfen', 'maada', 'madem', 'mademki', 'mamafih', 'mebni', 'meğer', 'meğerki',
    'meğerse', 'naşi', 'nasıl', 'nasılsa', 'nazaran', 'neden', 'nedeniyle', 'nedense', 'nerde', 'nerden', 'nerdeyse',
    'nere', 'nerede', 'nereden', 'neredeyse', 'neresi', 'nereye', 'netekim', 'neye', 'neyi', 'neyse', 'nice', 'niçin',
    'nihayet', 'nihayetinde', 'nitekim', 'niye', 'öbür', 'öbürkü', 'öbürü', 'oldu', 'oldukça', 'olur', 'ona', 'onca',
    'önce', 'önceden', 'önceleri', 'öncelikle', 'onculayın', 'onda', 'ondan', 'onlar', 'onu', 'onun', 'oracık',
    'oracıkta', 'orada', 'oradan', 'oranca', 'oranla', 'oraya', 'öteki', 'ötekisi', 'öyle', 'öylece', 'öylelikle',
    'öylemesine', 'oysa', 'oysaki', 'öz', 'pek', 'pekala', 'pekçe', 'peki', 'peyderpey', 'rağmen', 'sadece', 'sahi',
    'sahiden', 'sana', 'sanki', 'şayet', 'sen', 'siz', 'sonra', 'sonradan', 'sonraları', 'sonunda', 'şöyle', 'şuna',
    'şuncacık', 'şunda', 'şundan', 'şunlar', 'şunu', 'şunun', 'şura', 'şuracık', 'şuracıkta', 'şurası', 'tabii', 'tam',
    'tamam', 'tamamen', 'tamamıyla', 'tek', 'üzere', 'vasıtasıyla', 'velev', 'velhasıl', 'velhasılıkelam', 'veya',
    'veyahut', 'yahut', 'yakında', 'yakından', 'yakinen', 'yakınlarda', 'yalnız', 'yalnız', 'yalnızca', 'yani',
    'yeniden', 'yenilerde', 'yine', 'yok', 'yoksa', 'yoluyla', 'yüzünden', 'zarfında', 'zaten', 'zati', 'zira'
}


PARA_DICT = {}  # { <paragraph_id:int> -> <paragraph:str> }
PARA_TOKENS_DICT = {}  # { <paragraph_id:int> -> [<token1:str>, <token2:str>, ...] }
PARA_BIGRAMS_DICT = {}  # { <paragraph_id:int> -> {<bigram1:str>, <bigram2:str>, ...} }

PARA_TF_DICT = {}  # { <paragraph_id:int> -> { <term:str> -> <frequency:int> } }
TERM_DF_DICT = {}  # { <term:string> -> <document_frequency:int> }, Document = Paragraph

PARA_TF_IDF_DICT = {}  # { <paragraph_id:int> -> { <term:str> -> <tf_idf_score:float>} }


def preprocess_text(text):
    """Returns the unicode-normalized and preprocessed`text`."""
    result = unicodedata.normalize('NFC', text)
    result = re.sub(u'\xad', '', result)  # Remove soft-hypen characters
    result = re.sub(r' \((Fotoğraf|Görsel|Grafik|Harita|Resim|Şekil|Şema|Tablo).+?\)', '', result)
    result = re.sub(r'(?<=\d)\.(?=\d)', '', result)  # Remove dot (.) between digits

    return result.translate(str.maketrans("âîôû", "aiou")).strip()  # Normalize a, i ,o, u


def tokenize(text):
    """
    Returns a List of tokens (words) for given `text`.
    Tokenization includes removing characters other than letters and digits, and lower-casing.
    """
    return [token.lower() for token in re.sub(TOKEN_SEPARATOR_RGX, " ", text).split()]


def length_of_vector(vector):
    """
    Returns the length (magnitude) of given `vector`.
    """
    return round(math.sqrt(sum([elem**2 for elem in vector])), 5)


def tf_idf_score(tf, df, N):
    """
    Calculates tf-idf score of a term t in a document d.
    :param tf: Frequency of t in d (int, 0 <= tf)
    :param df: Document frequency of t (int, 0 < df <= N)
    :param N: Number of documents (int)
    """
    return 0.0 if tf == 0 else round((1 + math.log10(tf)) * math.log10(N/df), 5)


def get_tf_dict(text):
    """Returns Term-Frequency Dictionary for given `text`."""
    tf_dict = {}  # { <term:str> -> <frequency:int> }
    for token in tokenize(text):
        if token not in tf_dict:
            tf_dict[token] = 1
        else:
            tf_dict[token] += 1

    return tf_dict


def cosine_similarity(vec1, vec2):
    """
    Returns the cosine similarity of the given vectors `vec1` and `vec2`.
    """
    dot_product = sum([e1*e2 for (e1, e2) in zip(vec1, vec2)])
    return 0.0 if dot_product == 0 else round(dot_product/(length_of_vector(vec1) * length_of_vector(vec2)), 5)


def lcs_len(X, Y):
    """Returns the length of Longest Common Sequence between given collections `X` and `Y`"""
    m = len(X)
    n = len(Y)
    L = [[0] * (n+1) for _ in range(m+1)]
    for i in range(m+1):
        for j in range(n+1):
            if i == 0 or j == 0:
                continue
            elif X[i-1] == Y[j-1]:
                L[i][j] = L[i-1][j-1] + 1
            else:
                L[i][j] = max(L[i-1][j], L[i][j-1])

    return round(L[m][n], 5)


def ngrams(n, X, is_word=False):
    result = set()
    join_with = '' if is_word else ' '
    for i in range(len(X) - n + 1):
        result.add(join_with.join(X[i:i+n]))

    return result


def char_bigrams(tokens):
    """Returns the set of the character bigrams of the tokens stored in the given list `tokens`."""
    return {bg for t in tokens for bg in ngrams(2, t, is_word=True)}


def jaccard(set1, set2):
    """Returns the Jaccard similarity coefficient for given sets `set1` and `set2`"""
    intersection_size = len(set1 & set2)
    return 0.0 if intersection_size == 0 else round(intersection_size / len(set1 | set2), 5)


def get_paras(q, top_k=1):
    """
    Returns `top_k` related paragraphs for given question `q`.
    Returns a List of paragraphs ids in the order of relattion (similarity) to the given `q`.
    """
    q_tf_dict = get_tf_dict(q)
    q_tokens = tokenize(q)
    q_terms = set(q_tf_dict) - STOPWORDS
    q_tf_idf_dict = {term: tf_idf_score(freq, TERM_DF_DICT.get(term, 1), len(PARA_DICT))
                     for (term, freq) in q_tf_dict.items()}
    q_vec = [q_tf_idf_dict[term] for term in q_terms]
    q_bigrams = ngrams(2, q_tokens)  # Word bigrams

    similarity_para_tuples = []  # A List of (<similarity_score>, <para_id>) Tuples
    for para_id in PARA_DICT:
        if len(PARA_TOKENS_DICT[para_id]) < MIN_PARA_LEN:
            continue

        para_terms = set(PARA_TF_DICT) - STOPWORDS
        common_terms = q_terms & para_terms

        # (Question) Term Frequency Score
        tfi_score = 0.0
        for term in common_terms:
            tfi_score += tf_idf_score(PARA_TF_DICT[para_id].get(term, 0), TERM_DF_DICT.get(term, 1), len(PARA_DICT))
        tfi_score = round(tfi_score, 5)

        # Cosine Similarity Score
        para_vec = [PARA_TF_IDF_DICT[para_id].get(term, 0) for term in q_terms]
        cos_score = cosine_similarity(q_vec, para_vec)

        # Longest Common Subsequence Score
        lcs_score = round(lcs_len(q_tokens, PARA_TOKENS_DICT[para_id]) / len(q_tokens), 5)

        # Word Bigrams Score
        para_bigrams = PARA_BIGRAMS_DICT[para_id]
        common = len(q_bigrams & para_bigrams)
        big_score = round(common / len(q_bigrams), 5)

        # Jaccard Score of Terms
        jac_score = jaccard(q_terms, para_terms)

        net_score = 0.28 * tfi_score + 8 * cos_score + 4 * lcs_score + 3 * big_score + 5 * jac_score
        similarity_para_tuples.append((net_score, para_id))

    return [para_id for (_, para_id) in sorted(similarity_para_tuples, reverse=True)[:top_k]]


#
# PART 2: QUESTION ANSWERING
#

# Questions with numerical answers (e.g., 1995)
Q_NUMBER = ['kaç', 'tarihte', 'hangi (yıl|sene)', 'ne kadar', 'ne zaman']
Q_NUMBER_RGX = f" ({'|'.join(Q_NUMBER)})"


def common_front_score(str1, str2):
    """
    Returns the number of common characters (matching from the beginning) divided by the length of the longest string.
    """
    result = 0
    min_len = min(len(str1), len(str2))
    max_len = max(len(str1), len(str2))
    for i in range(min_len):
        if str1[i] == str2[i]:
            result += 1

    return result / max_len


def get_sentence(q, para_ids):
    """
    Returns the most related sentence (of the given paragraphs) for the given question `q`.
    `para_ids` is a list of paragraph idss.
    """
    sentences = [s for para_id in para_ids for s in re.split(SENTENCE_SEPARATOR_RGX, PARA_DICT[para_id])]
    q_bigs = char_bigrams(tokenize(q))  # Character bigrams
    # A List of (<common_bigrams_number>, <sentence_index>) Tuples
    big_score_sent_tuples = [(len(q_bigs & char_bigrams(tokenize(s))), i) for (i, s) in enumerate(sentences)]

    return sentences[sorted(big_score_sent_tuples, reverse=True)[0][1]]


def extract_answer(q, s):
    """
    Extracts and returns the answer to the given question `q` from the given sentence `s`.
    """
    q = re.sub(r'\'[^\s]+', '', q)  # Removes apostrophes with related suffixes from the question
    s = re.sub(r'\'[^\s]+', '', s)  # Removes apostrophes with related suffixes from the sentence

    q_tokens = tokenize(q)
    q_terms = set(q_tokens)

    s_tokens = tokenize(s)
    s_terms = set(s_tokens)
    common_terms = set()  # Terms that exist both in the question and the sentence

    for q_term in q_terms:
        for s_term in s_terms:
            if common_front_score(q_term, s_term) >= 0.5:
                common_terms.add(s_term)

    # Removes tokens of sentences that are similar to any tokens of the question
    answer_tokens = [t for t in s_tokens if t not in common_terms]

    ans = None  # Answer to be returned
    if re.search("hangi çağ", q):  # Handles "hangi çağ" type questions
        cag_index = -1  # Index of the first "çağ" token
        for (i, token) in enumerate(s_tokens):
            if token == 'çağ':
                cag_index = i
                break

        if cag_index > 0:
            ans = ' '.join(s_tokens[cag_index - 1:cag_index + 1])  # Takes the word "çağ" and the one before it

    else:
        if len(answer_tokens) > 0:
            if re.search(Q_NUMBER_RGX, q):  # Handles numeric type ('kaç', 'hangi yıl', etc.) type questions
                numbers = re.findall(r'\d+', ' '.join(answer_tokens))
                if len(numbers) > 0:
                    ans = ' '.join(numbers)

            elif re.search("nasıl", q):  # Questions with verb answers (e.g., 'arttırır')
                ans = answer_tokens[-1]

        else:
            # Removes tokens of sentences that are the exact same as any tokens of the question
            answer_tokens = [t for t in s_tokens if t not in q_terms]
            if len(answer_tokens) == 0:
                ans = s
            else:
                ans = ' '.join(answer_tokens)

    return ans if ans else ' '.join(answer_tokens)


# Reads corpus file
with open(DERLEM_PATH, encoding='utf16') as f:
    for line in f.readlines():
        line = preprocess_text(line)
        if len(line) != 0:
            para_id, paragraph = line.split(' ', 1)
            para_id = int(para_id)
            PARA_DICT[para_id] = paragraph
            PARA_TOKENS_DICT[para_id] = tokenize(paragraph)
            PARA_BIGRAMS_DICT[para_id] = ngrams(2, PARA_TOKENS_DICT[para_id])
            PARA_TF_DICT[para_id] = get_tf_dict(paragraph)  # { <term:str> -> <frequency:int> }


# Constructs TERM_DF_DICT
for tf_dict in PARA_TF_DICT.values():
    for term in tf_dict:
        if term not in TERM_DF_DICT:
            TERM_DF_DICT[term] = 1
        else:
            TERM_DF_DICT[term] += 1


# Constructs PARA_TF_IDF_DICT
for para_id in PARA_TF_DICT:
    PARA_TF_IDF_DICT[para_id] = {term: tf_idf_score(freq, TERM_DF_DICT[term], len(PARA_DICT))
                                 for (term, freq) in PARA_TF_DICT[para_id].items()}


with open(TEST_Q_PATH, 'r', encoding='utf16') as f:
    TEST_Q = [preprocess_text(line) for line in f if len(line.strip()) > 0]


PRED_PARAS = []
PRED_ANSWERS = []

for (i, q) in enumerate(TEST_Q):
    print("Processing Question {0:04} ...\t".format(i+1))

    q_pred_paras = get_paras(q, 3)  # Gets the most related 3 paragraphs for the question `q`
    q_pred_para = q_pred_paras[0]   # First is the most related paragraph
    q_pred_answer = extract_answer(q, get_sentence(q, q_pred_paras))

    PRED_PARAS.append(str(q_pred_para))
    PRED_ANSWERS.append(q_pred_answer)


with open(os.path.join(TASK1_PRED_PATH, '{0}.txt'.format(GROUP_ID)), 'w', encoding='utf16') as f:
    f.write('\n'.join(PRED_PARAS))


with open(os.path.join(TASK2_PRED_PATH, '{0}.txt'.format(GROUP_ID)), 'w', encoding='utf16') as f:
    f.write('\n'.join(PRED_ANSWERS))

