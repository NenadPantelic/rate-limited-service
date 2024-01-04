from random import shuffle, choice
from string import ascii_lowercase, ascii_uppercase
from uuid import uuid4

NUM_OF_USERS = 50
NUM_OF_TOKENS = 10_000

letters = list(ascii_lowercase) + list(ascii_uppercase)
shuffle(letters)

usernames = []
user_ids = []
tokens = []
# generate usernames and user ids
for _ in range(NUM_OF_USERS):
    usernames.append(''.join([choice(letters) for _ in range(10)]))
    user_ids.append(uuid4())
    tokens.append(uuid4())

tokens = [uuid4() for _ in range(NUM_OF_TOKENS)]

user_context_list = []
counter = 0
distribution_factor = NUM_OF_TOKENS // NUM_OF_USERS


for i in range(NUM_OF_USERS):
    user_id = user_ids[i]
    username = usernames[i]
    for _ in range(distribution_factor):
        user_context_list.append((user_id, username, tokens[counter]))
        counter += 1

shuffle(user_context_list)

with open('auth.csv', 'a') as fout:
    fout.write('id,username,token\n')
    for user_context in user_context_list:
        fout.write(f'{user_context[0]},{user_context[1]},{user_context[2]}\n')

