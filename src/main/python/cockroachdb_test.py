#!/usr/bin/python3


import psycopg2
import pandas as pd

# Connect to the "bank" database.
connection = psycopg2.connect(database='insight', user='maxroach', host='ec2-52-36-220-17.us-west-2.compute.amazonaws.com', port=26257)


query = """
SELECT * FROM test;
"""

df = pd.read_sql(query, connection)


print(df)


# # Make each statement commit immediately.
# conn.set_session(autocommit=True)
#
# # Open a cursor to perform database operations.
# cur = conn.cursor()
