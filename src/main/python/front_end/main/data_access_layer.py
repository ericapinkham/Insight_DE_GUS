#!/usr/bin/python3
import psycopg2
import pandas as pd

def fetch(query):
    # connection = psycopg2.connect(database='insight', user='maxroach', host='ec2-52-36-220-17.us-west-2.compute.amazonaws.com', port = 26257)
    connection = psycopg2.connect(database='insight', user='maxroach', host='internal-CockroachDB-408925475.us-west-2.elb.amazonaws.com', port = 26257)
    df = pd.read_sql(query, connection)
    connection.close()
    return df

def get_most_used_languages(language, date):
    query = """
        SELECT  import_name,
                usage_count
            FROM daily_import_summary
            WHERE language_name = '{}'
                AND summary_date = '{}'
            ORDER BY usage_count DESC
            LIMIT 10
        """.format(language, date)
    return fetch(query)

def get_language_totals(date):
    query = """
        SELECT  language_name,
                total_daily_usage
            FROM daily_language_totals
            WHERE commit_date = '{}'
            """.format(date)
    return fetch(query)

def get_usage_by_import(language, packages, begin_date, end_date):
    pivots = ",".join(["""SUM(CASE WHEN import_name = '{0}' THEN usage_count ELSE 0 END) "{0}" """.format(package) for package in packages])
    in_clause = ",".join(["'{}'".format(package) for package in packages])

    query = """
        SELECT  commit_date,
                {}
            FROM commits
            WHERE language_name = '{}'
                AND import_name IN ({})
                AND commit_date BETWEEN '{}' AND '{}'
            GROUP BY commit_date
            ORDER BY commit_date;
        """.format(pivots, language, in_clause, begin_date, end_date)
    return fetch(query)

def get_packages_by_language(language, date):
    query = """
        SELECT  import_name
            FROM daily_import_summary
            WHERE language_name = '{}'
                AND summary_date = '{}'
            ORDER BY usage_count DESC
            LIMIT 5000
        """.format(language, date)
    return fetch(query)

def get_unique_languages():
    query = """
        SELECT  DISTINCT(language_name) language
            FROM imports
        """
    return fetch(query)
