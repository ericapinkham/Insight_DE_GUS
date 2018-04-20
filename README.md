# Insight Data Engineering Project 17B

## Project Idea (1-sentence):
Monitor package usage and discussion to enable content creators insights into new and trending packages.

## What is the purpose, and most common use cases?
Monitoring of package usage and mentions in social media to gain insights into popularity of packages.
* Create tutorials
* Write more detailed documentation
* Write books

## Which technologies are well-suited to solve those challenges? (list all relevant)
* Ingestion and Processing
..* Kafka
..* Spark Streaming
..* Spark

* Storage
..* AWS Redshift
..* Casandra
..* Vertica
..* CouchDB
..* Riak
..* MySQL

* Front End
..* Play Framework

## Proposed architecture
Kafka => Spark Streaming => AWS Redshift => Spark => MySQL => Play Framework
