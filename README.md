# Insight Data Engineering Project 18B

## Project Idea (1-sentence):
Monitor package usage and discussion to enable content creators to gain insights into new and trending packages.

## What is the purpose, and most common use cases?
Provide an analytics dashboard monitoring package usage and discussion in social media to gain insights into popularity of packages.

### This could be used to:
* Create Sweet Blogs About New and Interesting Packages
* Understand Package Adoption and Churn
* Write Books
* Enable Developers to Understand Package Usage
* Give developers a sense of which packages are being used for what purposes in their language of choice

### Some possibly useful things to track:
* Metrics:
  * Package "Churn"
  * Trending packages (mentions on social media)
  * Inclusion normalized by "active" repositories
  * Which functions are being used?
* Dimensions:
  * Programming Language?
  * Domain (i.e. slicing by "dataframes" provides results for Pandas etc)?
  * Programming language characteristics
     * functional, imperative
     * object oriented
     * compiled, interpreted

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
![Data Engineering Stack](./de_stack.png)
