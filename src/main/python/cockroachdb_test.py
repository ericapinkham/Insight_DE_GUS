#!/usr/bin/python3


import psycopg2
import pandas as pd

# Connect to the "bank" database.
connection = psycopg2.connect(database='insight', user='maxroach', host='ec2-52-36-220-17.us-west-2.compute.amazonaws.com', port=26257)
connection.set_session(autocommit=True)
cur = connection.cursor()

# try:
cur.execute("""INSERT INTO commits
	SELECT commit_date,
		language_name,
		import_name,
		usage_count
		FROM received_commits
		WHERE received_date = '2018-05-07'
		ON CONFLICT (commit_date, language_name, import_name) DO UPDATE SET usage_count = commits.usage_count + excluded.usage_count;""")
# except:
# 	raise ValueError('Error executing commit update')

# a = cur.execute("insert into test values ('dog');")
cur.close()
connection.close()



# # Make each statement commit immediately.
# conn.set_session(autocommit=True)
#
# # Open a cursor to perform database operations.
# cur = conn.cursor()
