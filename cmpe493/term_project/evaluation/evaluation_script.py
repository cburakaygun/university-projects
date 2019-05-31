import string

import os
from pathlib import Path
import subprocess
import numpy as np
import pandas as pd
from sklearn.metrics import accuracy_score
from nltk import jaccard_distance
#%%
def preprocess_answer(answer):
    tokens = answer.split()
    normalized_tokens = []
    for token in tokens:
        if not any(char.isdigit() for char in token):
            normalized_tokens.append(token.lower().translate(
                    str.maketrans('', '', string.punctuation)))
        else:
            normalized_tokens.append(token)
    return ' '.join(normalized_tokens)
#%%
def word_to_bigrams(word):
    if len(word) > 1:
        return {word[i:i+2] for i in range(len(word)-1)}
    return set(word)
#%%
def jaccard_score(answers, preds):
    return np.mean([1 - jaccard_distance(word_to_bigrams(answer), word_to_bigrams(pred))
                for answer, pred in zip(answers, preds)])    
        
#%%
test_questions_path = os.path.abspath('./test_data/test_questions.txt')
task1_answers_path = os.path.abspath('./test_data/task1_answers.txt')
task2_answers_path = os.path.abspath('./test_data/task2_answers.txt')

submissions_path = os.path.abspath('./submissions/')
task1_predictions_path = os.path.abspath('./task1_predictions/')
task2_predictions_path = os.path.abspath('./task2_predictions/')
#%%
with open(test_questions_path, encoding='utf16') as f:
    questions = f.readlines()

with open(task1_answers_path, encoding='utf16') as f:
    task1_answers = [int(l) for l in f.readlines()]
    
with open(task2_answers_path, encoding='utf16') as f:
    task2_answers = [preprocess_answer(l) for l in f.readlines()]
#%%

submission_ids = os.listdir(submissions_path)
runnable_submissions, non_runnable_submissions = [], []
for submission_id in submission_ids:
    
    script_path = Path(submissions_path) / submission_id / (submission_id + '.py')
    try:
        print('run ', submission_id)
        code_output = subprocess.run('python3 {sp} {tqp} {t1p} {t2p}'.format(sp=script_path,
                                                     tqp=test_questions_path,
                                                     t1p=task1_predictions_path,
                                                     t2p=task2_predictions_path), shell=True)
        if code_output.returncode == 0:
            runnable_submissions.append(submission_id)
        else:
            non_runnable_submissions.append(submission_id)
    except Exception as e: 
        print(e)
        print('not run ', submission_id)
        non_runnable_submissions.append(submission_id)
    
#%%
group_to_task1_predictions, group_to_task2_predictions = {}, {}
for submission_id in runnable_submissions:
    with open(Path(task1_predictions_path) / (submission_id + '.txt'), encoding='utf16') as f:
        task1_predictions = [int(l) for l in f.readlines()]
        group_to_task1_predictions[submission_id] = task1_predictions
    
    with open(Path(task2_predictions_path) / (submission_id + '.txt'), encoding='utf16') as f:
        task2_predictions = [preprocess_answer(l) for l in f.readlines()]
        group_to_task2_predictions[submission_id] = task2_predictions
#%%
group_to_task1_accuracy = {group: accuracy_score(task1_answers, preds)
                            for group, preds in group_to_task1_predictions.items()}

group_to_task2_accuracy = {group: accuracy_score(task2_answers, preds)
                            for group, preds in group_to_task2_predictions.items()}

group_to_task2_jaccard = {group: jaccard_score(task2_answers, preds)
                            for group, preds in group_to_task2_predictions.items()}
#%%
grades = pd.DataFrame([group_to_task1_accuracy, group_to_task2_accuracy, 
                       group_to_task2_jaccard]).T
grades.columns = ['Task1-Accuracy', 'Task2-Accuracy', 'Task2-Jaccard']
grades.to_csv('grades.csv')





