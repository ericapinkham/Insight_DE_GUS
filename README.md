# Insight Data Engineering Project 18B

## Project Idea (1-sentence):
Monitor package usage and discussion to enable content creators to gain insights into new and trending packages.

## What is the purpose, and most common use cases?
Provide an analytics dashboard monitoring package usage and discussion in social media to gain insights into popularity of packages.

### This could be used to:
* Create Sweet Blogs About New and Interesting Packages: "I wrote a blog post about Pandas, now what?"
* Understand Package Adoption and Churn: "We implemented a sweet feature and look at our adoption!"
* Write Books: "Which packages should we include in our book about Haskell?"
* Enable Developers to Understand Package Usage: "We intended our package to be used for X but almost everyone uses it for Y."

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
