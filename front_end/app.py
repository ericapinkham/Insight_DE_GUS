#!/usr/bin/python3
# -*- coding: utf-8 -*-
import dash
import dash_core_components as dcc
import dash_html_components as html
import mysql.connector as sql
import pandas as pd

# Set up the MySQL Connection
db_connection = sql.connect(host='ec2-35-161-183-67.us-west-2.compute.amazonaws.com', port='3306', database='insight', user='root', password='password')

# Here's my cool App
app = dash.Dash()

colors = {
    'background': '#111111',
    'text': '#7FDBFF'
}

title = html.H1(
    children='Package Tracker',
    style={'textAlign': 'center', 'color': colors['text']}
    )
subtitle = html.Div(
    children='Insight Data Engineering Rocks',
    style={'textAlign': 'center', 'color': colors['text']}
    )

markdown_text = '''
# markdown_text

this is text explaining the project?

* whatever
* whatever 1
  * whatever
'''
intro = dcc.Markdown(children = markdown_text)

# Get a list of unique languages from MySQL. This should probably be precomputed
# df = pd.read_sql('SELECT * FROM GitHubData', con=db_connection)
unique_languages = pd.read_sql('SELECT DISTINCT(language_name) language FROM GitHubData', db_connection)
language_dropdown = dcc.Dropdown(
    id = 'language_dropdown',
    options=[{'label': l, 'value': l} for l in unique_languages.language],
    value='MTL'
)

app.layout = html.Div(style={'backgroundColor': colors['background'], 'color': colors['text']}, children = [
    title,
    subtitle,
    intro,
    html.Div(style={'columnCount': 2},
    children=[
    html.Label('Language'),
    language_dropdown,
    html.Label('Package'),
    dcc.Dropdown(id='package_dropdown'),
    ])
    ])

@app.callback(
    dash.dependencies.Output('package_dropdown', 'options'),
    [dash.dependencies.Input('language_dropdown', 'value')])
def language_dropdown(language):
    unique_packages = pd.read_sql("SELECT DISTINCT(package_name) package FROM GitHubData WHERE language_name = '{}'".format(language), db_connection)
    return [{'label': l, 'value': l} for l in unique_packages.package]

package_dropdown = dcc.Dropdown(
    options=[],
    value=['MTL', 'SF'],
    multi=True
)





if __name__ == '__main__':
    app.run_server(debug=True)
