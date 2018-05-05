import psycopg2
import pandas as pd

def fetch(query):
    connection = psycopg2.connect(database='insight', user='maxroach', host='ec2-52-36-220-17.us-west-2.compute.amazonaws.com', port=26257)
    # connection.sql_mode = [SQLMode.ANSI_QUOTES]
    df = pd.read_sql(query, connection)
    connection.close()
    return df

def get_most_used_languages(language, begin_date, end_date):
    query = """
        SELECT  import_name "Import",
                SUM(usage_count) "Usage"
            FROM GitHubData
            WHERE language_name = '{}'
                AND commit_date BETWEEN '{}' AND '{}'
            GROUP BY import_name
            ORDER BY 2 DESC
            LIMIT 10
        """.format(language, begin_date, end_date)
    return fetch(query)

def get_usage_by_import(language, packages, begin_date, end_date):
    pivots = ",".join(["""SUM(CASE WHEN import_name = '{0}' THEN usage_count ELSE 0 END) "{0}" """.format(package) for package in packages])
    in_clause = ",".join(["'{}'".format(package) for package in packages])

    query = """
        SELECT  commit_date,
                {}
            FROM GitHubData
            WHERE import_name IN ({})
                AND commit_date BETWEEN '{}' AND '{}'
            GROUP BY commit_date
            ORDER BY commit_date;
        """.format(pivots, in_clause, begin_date, end_date)
    return fetch(query)

def get_packages_by_language(language):
    query = """
        SELECT  DISTINCT(import_name) package
                FROM GitHubData
            WHERE language_name = '{}'
        """.format(language)
    return fetch(query)

def get_unique_languages():
    query = """
        SELECT  DISTINCT(language_name) language
            FROM GitHubData
        """
    return fetch(query)
