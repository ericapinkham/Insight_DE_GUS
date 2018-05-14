#!/usr/bin/python3
# -*- coding: utf-8 -*-
import dash
import dash_core_components as dcc
import dash_html_components as html

import pandas as pd
from datetime import date, timedelta, datetime
import plotly.graph_objs as go
from dash.dependencies import Input, Output, State
import colorsys
from flask import Flask
import data_access_layer as dal

# Here"s my cool App
server = Flask(__name__)
app = dash.Dash(__name__, server = server)

# Constants
colors = {
    "blue": "#4285f4",
    "white": "#fff2ff"
}

LANGUAGES = [
    "C#",
    "F#",
    "Haskell",
    "Java",
    "JavaScript",
    "Kotlin",
    "Python",
    "Rust",
    "Scala",
    "Swift",
    "TypeScript"
]

title_font = {"size": 24}
annotation_font = {"size": 16}

# Layout
def serve_layout():
    return html.Div(
        style = {"backgroundColor": colors["blue"], "color": colors["white"]},
        className = "ten columns offset-by-one",
        children = [
            html.Div(
                style = {"margin-left": "30", "margin-right": "30", "margin-bottom": "30", "margin-top": "30"},
                children = [
                    html.Div([
                        html.Div([html.H1("GUS: GitHub Usage Statistics")],
                            className = "eleven columns"
                            ),
                        html.Div([html.Img(src = "https://raw.githubusercontent.com/ericapinkham/Insight_DE_GUS/master/src/main/resources/GUS_logo.png", width = "100%")],
                            className = "one columns"
                            )
                        ],
                        className = "row"
                        ),
                    html.Div(style = {"color": colors["blue"]},
                    className = "row",
                    children = [
                    html.Div([
                        html.H5("Language", style = {"color": colors["white"], "height": "38"}),
                        dcc.Dropdown(
                            id = "language_dropdown",
                            options = [{"label": l, "value": l} for l in LANGUAGES],
                            placeholder = "Select a language"
                        )],
                        className = "two columns"
                        ),
                    html.Div([
                            html.H5("Import", style = {"color": colors["white"], "height": "38"}),
                            dcc.Dropdown(
                                id = "import_dropdown",
                                options = [],
                                multi = True,
                                placeholder = "Select an import",
                            )],
                        className = "five columns"
                        ),
                    html.Div([html.H5("Evaluation Date", style = {"color": colors["white"]}),
                            dcc.DatePickerRange(
                                id = "eval_date",
                                start_date = date.today() - timedelta(1) - timedelta(weeks = 1),
                                end_date = date.today() - timedelta(1)
                            )
                            ],
                        className = "three columns"
                        ),
                    html.Div([
                        html.Button("Fetch", id = "fetch_button",
                        style = {
                            "color": colors["blue"],
                            "backgroundColor": colors["white"],
                            "height": "38",
                            "width": "285%",
                            "marginTop": "45",
                            "marginLeft": "0",
                            "fontSize": "16"
                            })],
                        className = "one columns"
                        )
                    ]),
                    html.Div([
                        html.Div([
                            dcc.Graph(
                                style={"height": 300},
                                id = "imports_by_date_graph"
                                )],
                            className = "twelve columns",
                            style={"margin-top": "10"}
                            )],
                    className = "row",
                    ),
                    html.Div([
                        html.Div([dcc.Graph(id = "import_summary_bar", style = {"height": 400})],
                            className = "six columns",
                            style={"margin-top": "20"}
                            ),
                        html.Div([dcc.Graph(id = "language_share_pie", style = {"height": 400})],
                            className = "six columns",
                            style={"margin-top": "20"}
                            )
                        ],
                        className = "row"
                    ),
            ])
    ])
app.layout = serve_layout()
app.css.append_css({
    "external_url": "https://codepen.io/chriddyp/pen/bWLwgP.css"
})

@app.callback(
    Output("language_share_pie", "figure"),
    [Input("fetch_button", "n_clicks")],
    [State("eval_date", "end_date")])
def refresh_language_share_pie(clicks, date):
    language_totals = dal.get_language_totals(date)
    figure = go.Figure(
        data = [go.Pie(
            type = "pie",
            labels = language_totals["language_name"],
            values = language_totals["total_daily_usage"],
            name = "Imports by Language",
            hole = 0.5,
            # domain = {"x": [.3, 1]},
            rotation = -135
            )],
        layout = go.Layout(
            title = "Imports by Language for {}".format(convert_date(date)),
            titlefont = title_font,
            font = annotation_font,
            legend = go.Legend(x = 0, y = 1.0, font = {"size": 12}, orientation = "v")
            )
        )
    return figure

@app.callback(
    Output("import_dropdown", "options"),
    [Input("language_dropdown", "value")],
    [State("eval_date", "end_date")])
def language_dropdown(language, date):
    unique_packages = dal.get_packages_by_language(language, date)
    return [{"label": l, "value": l} for l in unique_packages.import_name]

@app.callback(
    Output("import_summary_bar", "figure"),
    [Input("fetch_button", "n_clicks")],[State("language_dropdown", "value"), State("eval_date", "end_date")])
def refresh_import_summary_bar(clicks, language, end_date):
    package_data = dal.get_most_used_languages(language, end_date)
    return go.Figure(
            data = [go.Bar(
                x = package_data["usage_count"][::-1],
                y = package_data["import_name"][::-1],
                name = "Imports",
                orientation = "h"
            )],
            layout = go.Layout(
                title = "Top 10 Imports for {} on {}".format(language, convert_date(end_date)),
                titlefont = title_font,
                font = annotation_font,
                margin = go.Margin(l = 200, r = 40, t = 40, b = 30)
            )
        )

@app.callback(
    Output("imports_by_date_graph", "figure"),
    [Input("fetch_button", "n_clicks")],
    [State("language_dropdown", "value"), State("import_dropdown", "value"),
    State("eval_date", "start_date"), State("eval_date", "end_date")])
def refresh_imports_by_date_graph(clicks, language, packages, start_date, end_date):
    if packages == None:
        return None

    package_data = dal.get_usage_by_import(language, packages, start_date, end_date)
    colors = gen_colors(len(packages))

    def make_trace(df, package, rgb):
        x_date = df["commit_date"]
        y_package = df[package]
        return go.Trace(
            x = x_date,
            y = y_package,
            name = package,
            marker = go.Marker(color = rgb)
            )
    return go.Figure(
            data=[make_trace(package_data, package, colors[i]) for i, package in enumerate(packages)],
            layout = go.Layout(
                title = "Daily Imports for {} to {}".format(convert_date(start_date), convert_date(end_date)),
                titlefont = title_font,
                showlegend = True,
                legend = go.Legend(x = 0, y = 1.0, font = annotation_font),
                margin = go.Margin(l = 40, r = 40, t = 40, b = 30)
                )
            )

def gen_colors(n):
    HSV_tuples = [(x*1.0/n, 0.5, 0.5) for x in range(n)]
    return list(map(lambda x: colorsys.hsv_to_rgb(*x), HSV_tuples))

def convert_date(date):
    return datetime.strftime(datetime.strptime(date,"%Y-%m-%d"), "%b %d, %Y")

if __name__ == "__main__":
    app.run_server(debug = True, host = "0.0.0.0")
