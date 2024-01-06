from random import shuffle, choice
from string import ascii_lowercase, ascii_uppercase
from uuid import uuid4

NUM_OF_USERS = 50
NUM_OF_API_KEYS = 10_000
PRICING_PLANS = ['FREE', 'BASIC', 'PRO', 'BUSINESS']
letters = list(ascii_lowercase) + list(ascii_uppercase)
shuffle(letters)

usernames = []
user_ids = []
api_keys = []
# generate usernames and user ids
for _ in range(NUM_OF_USERS):
    usernames.append(''.join([choice(letters) for _ in range(10)]))
    user_ids.append(uuid4())

api_keys = [uuid4() for _ in range(NUM_OF_API_KEYS)]

user_context_list = []
counter = 0
distribution_factor = NUM_OF_API_KEYS // NUM_OF_USERS


for i in range(NUM_OF_USERS):
    user_id = user_ids[i]
    username = usernames[i]
    for _ in range(distribution_factor):
        user_context_list.append((user_id, username, api_keys[counter]))
        counter += 1

shuffle(user_context_list)

with open('auth.csv', 'a') as fout:
    fout.write('id,username,api_key,pricing_plan\n')
    for user_context in user_context_list:
        fout.write(f'{user_context[0]},{user_context[1]},{user_context[2]},{choice(PRICING_PLANS)}\n')

