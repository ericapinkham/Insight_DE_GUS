#!/usr/bin/python3
# -*- coding: utf-8 -*-
import dash
import dash_core_components as dcc
import dash_html_components as html

import pandas as pd
from datetime import date as dt
import plotly.graph_objs as go
from dash.dependencies import Input, Output, State
import colorsys
from flask import Flask
import data_access_layer as dal

# Set up the MySQL Connection

# Here"s my cool App
server = Flask(__name__)
app = dash.Dash(__name__, server=server)

colors = {
    "background": "#111111",
    "text": "#7FDBFF"
}

title = html.H1(
    children="Package Tracker",
    style={"textAlign": "center", "color": colors["text"]}
    )

subtitle = html.Div(
    children="Insight Data Engineering Rocks",
    style={"textAlign": "center", "color": colors["text"]}
    )

markdown_text = """
# Text Text Text

this is text explaining the project?

* whatever
* whatever 1
  * sub-whatever
"""
intro = dcc.Markdown(children = markdown_text)

# Get a list of unique languages from MySQL. This should probably be precomputed
# df = pd.read_sql("SELECT * FROM GitHubData", con=db_connection)

# Define the components
unique_languages = dal.get_unique_languages()
language_dropdown = dcc.Dropdown(
    id = "language_dropdown",
    options = [{"label": l, "value": l} for l in unique_languages.language]
)

package_dropdown = dcc.Dropdown(
    id = "package_dropdown",
    options = [],
    multi = True
)

begin_date = dcc.DatePickerSingle(
    id="begin_date",
    date=dt(2018, 1, 1)
)

end_date = dcc.DatePickerSingle(
    id="end_date",
    date=dt(2018, 5, 24)
)

graph = dcc.Graph(
    figure=go.Figure(
        data=[],
        layout=go.Layout(
            title='Packages!!!',
            showlegend=True,
            legend=go.Legend(
                x=0,
                y=1.0
            ),
            margin=go.Margin(l=40, r=0, t=40, b=30)
        )
    ),
    style={'height': 300},
    id='package_graph'
)

app.layout = html.Div( #style = {"backgroundColor": colors["background"], "color": colors["text"]},
    children = [
        title,
        subtitle,
        intro,
        html.Label("Language"),
        language_dropdown,
        html.Label("Dates"),
        html.Div(style = {"columnCount": 2},
            children = [
                begin_date,
                end_date
            ]),
        html.Label("Package"),
        package_dropdown,
        graph,
        html.Div(id='table_container')
])

@app.callback(
    Output("package_dropdown", "options"),
    [Input("language_dropdown", "value")])
def language_dropdown(language):
    unique_packages = dal.get_packages_by_language(language)
    return [{"label": l, "value": l} for l in unique_packages.package]

def generate_table(dataframe, max_rows=10):
    return html.Table(
        # Header
        [html.Tr([html.Th(col) for col in dataframe.columns])] +

        # Body
        [html.Tr([
            html.Td(dataframe.iloc[i][col]) for col in dataframe.columns
        ]) for i in range(min(len(dataframe), max_rows))]
    )

@app.callback(
    Output("table_container", "children"),
    [Input("language_dropdown", "value")],
    [State("begin_date", "date"), State("end_date", "date")])
def update_package_table(language, begin_date, end_date):
    package_data = dal.get_most_used_languages(language, begin_date, end_date)
    return generate_table(package_data)

@app.callback(
    Output("package_graph", "figure"),
    [Input("language_dropdown", "value"), Input("package_dropdown", "value")],
    [State("begin_date", "date"), State("end_date", "date")])
def update_graph(language, packages, begin_date, end_date):
    if packages == None:
        return go.Figure(
                data=[],
                layout=go.Layout(
                    title='Packages!!!',
                    showlegend=True,
                    legend=go.Legend(
                        x=0,
                        y=1.0
                    ),
                    margin=go.Margin(l=40, r=0, t=40, b=30)
                )
            )
    package_data = dal.get_usage_by_import(language, packages, begin_date, end_date)
    colors = gen_colors(len(packages))

    def make_trace(df, package, rgb):
        x_date = df['date']
        y_package = df[package]
        return go.Trace(
            x = x_date,
            y = y_package,
            name = package,
            marker = go.Marker(color = rgb)
            )

    return go.Figure(
            data=[make_trace(package_data, package, colors[i]) for i, package in enumerate(packages)],
            layout=go.Layout(
                title='Packages!!!',
                showlegend=True,
                legend=go.Legend(
                    x=0,
                    y=1.0
                ),
                margin=go.Margin(l=40, r=0, t=40, b=30)
            )
        )

def gen_colors(n):
    HSV_tuples = [(x*1.0/n, 0.5, 0.5) for x in range(n)]
    return list(map(lambda x: colorsys.hsv_to_rgb(*x), HSV_tuples))

if __name__ == "__main__":
    app.run_server(debug=True, host="0.0.0.0")
