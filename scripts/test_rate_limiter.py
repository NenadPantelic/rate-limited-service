import requests
from random import randint, choice
from time import sleep
from string import ascii_letters
from json import loads

# API keys
FREE_USER_API_KEY = 'b995f3be-660c-4b2b-809b-6351a13986a2'
BASIC_USER_API_KEY = 'd29710ed-4c69-48cb-8a8f-0b06f13fb9d8'
PRO_USER_API_KEY = 'fcbe8a5e-11ee-4a3a-b317-dbf71247cf1b'
BUSINESS_USER_API_KEY = 'ba3848cc-2d2b-4dee-88b9-ece926be01a6'

# endpoint definition
ENDPOINT_TEMPLATE = 'http://localhost:8083/api/v1/quotes?author={AUTHOR}&numOfQuotes={NUM_OF_QUOTES}'
AUTHOR_TEMPLATE_MARKER = '{AUTHOR}'
NUM_OF_QUOTES_MARKER = '{NUM_OF_QUOTES}'

format_endpoint = lambda author, num_of_quotes: ENDPOINT_TEMPLATE.replace(AUTHOR_TEMPLATE_MARKER, author).replace(
    NUM_OF_QUOTES_MARKER, num_of_quotes)


def execute_request_with_backoff(api_key: str):
    num_of_quotes = str(randint(1, 11))
    author = ''.join([choice(ascii_letters) for _ in range(10)])
    headers = {'Content-Type': 'application/json', 'x-Api-Key': api_key}

    attempts_counter = 0

    while attempts_counter < 3:
        response = requests.get(format_endpoint(author, num_of_quotes), headers=headers)

        response_headers = response.headers
        status_code = response.status_code

        print(f'Received status code {status_code}')

        if status_code == 200:
            print(f'Status - OK, rate limit token status = {response_headers.get("X-Rate-Limit-Remaining")}')
            return
        elif status_code == 429:
            sleep_period = int(response_headers.get("X-Rate-Limit-Retry-After-Seconds"))
            print(
                f'Rate limit exceeded. Waiting for {sleep_period} '
                f'seconds to refill.')
            attempts_counter += 1
            sleep(sleep_period)
        else:
            print(f'An error occurred: status={status_code}, output={loads(response.text)}')
            return


NUM_OF_ATTEMPTS = 1000

for _ in range(NUM_OF_ATTEMPTS):
    execute_request_with_backoff(BUSINESS_USER_API_KEY)
