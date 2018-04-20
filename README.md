# Insight Data Engineering Project 18B

## Project Idea (1-sentence):
Monitor package usage and discussion to enable content creators insights into new and trending packages.

## What is the purpose, and most common use cases?
Monitoring of package usage and mentions in social media to gain insights into popularity of packages.
* Create tutorials
* Write more detailed documentation
* Write books

## Which technologies are well-suited to solve those challenges? (list all relevant)
* Ingestion and Processing
  * Kafka
  * Spark Streaming
  * Spark

* Storage
  * AWS Redshift
  * Casandra
  * Vertica
  * CouchDB
  * Riak
  * MySQL
  * PostgreSQL

* Front End
  * Play Framework

## Proposed architecture
[GitHub, Reddit, StackOverflow, Twitter] => Kafka => Spark Streaming => Cassandra => Spark => PostgreSQL => Play Framework
