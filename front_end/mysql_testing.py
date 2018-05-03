#!/usr/bin/python3

import mysql.connector as sql
import pandas as pd
from datetime import date as dt

db_connection = sql.connect(host='ec2-35-161-183-67.us-west-2.compute.amazonaws.com', port='3306', database='insight', user='root', password='password')

# df = pd.read_sql('SELECT * FROM GitHubData', con=db_connection)
query = """
    SELECT  package_name "Package",
            SUM(usage_count) "Usage"
        FROM GitHubData
        WHERE language_name = '{}'
            -- AND date BETWEEN '{}' AND '{}'
        GROUP BY package_name
        ORDER BY 2 DESC
        LIMIT 10
""".format("scala", dt(2018,1,1), dt(2018,5,24))

packages = pd.read_sql(query, db_connection).to_dict('split')



print(packages)
