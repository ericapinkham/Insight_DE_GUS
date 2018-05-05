import mysql.connector as sql
from mysql.connector.constants import SQLMode
import pandas as pd

def fetch(query):
    connection = sql.connect(host="ec2-35-161-183-67.us-west-2.compute.amazonaws.com", port="3306", database="insight", user="root", password="password")
    connection.sql_mode = [SQLMode.ANSI_QUOTES]
    df = pd.read_sql(query, connection)
    connection.close()
    return df

def get_most_used_languages(language, begin_date, end_date):
    query = """
        SELECT  package_name "Package",
                SUM(usage_count) "Usage"
            FROM GitHubData
            WHERE language_name = '{}'
                AND date BETWEEN '{}' AND '{}'
            GROUP BY package_name
            ORDER BY 2 DESC
            LIMIT 10
        """.format(language, begin_date, end_date)
    return fetch(query)

def get_usage_by_import(language, packages, begin_date, end_date):
    pivots = ",".join(["""SUM(CASE WHEN package_name = '{0}' THEN usage_count ELSE 0 END) "{0}" """.format(package) for package in packages])
    in_clause = ",".join(["'{}'".format(package) for package in packages])

    query = """
        SELECT  date,
                {}
            FROM GitHubData
            WHERE package_name IN ({})
                AND date BETWEEN '{}' AND '{}'
            GROUP BY date
            ORDER BY date;
        """.format(pivots, in_clause, begin_date, end_date)
    return fetch(query)

def get_packages_by_language(language):
    query = """
        SELECT  DISTINCT(package_name) package
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
