# Insight Data Engineering Project 18B

## Project Idea (1-sentence):
Monitor package usage and discussion to enable content creators to gain insights into new and trending packages.

## What is the purpose, and most common use cases?
Provide an analytics dashboard monitoring package usage and discussion in social media to gain insights into popularity of packages.

### Primary Use Case:
Give content creators a sense of which packages are used and trending.

### Secondary Use Cases:
* Create Sweet Blogs About New and Interesting Packages
* Understand Package Adoption and Churn
* Write Books
* Enable Developers to Understand Package Usage
* Give developers a sense of which packages are being used for what purposes in their language of choice
* Tracking a user's use of a package. I.e. determining if a particular user actually knows a given package.

## Metrics
The main purpose here will be to track which packages are trending. test.

### Primary Metric
The primary metric, *popularity*, will be a weighted sum of the following:
* *insertions - deletions* of package in git hub diff files
  * normalize this by the total number of packages used
  * possibly restrict this to *active* repositories
* *mentions* of the package on various social media platforms
  * this needs to be normalized in some way
  * or it could be just a simple boost to the previous (purely additive)

Use the above to determine *trending* packages.
[This link](https://stackoverflow.com/questions/787496/what-is-the-best-way-to-compute-trending-topics-or-tags) might be useful.

### Secondary Metrics
Some possible secondary metrics to include if
* Package "Churn"
* Package inclusion normalized by "active" repositories
* Which functions are being used?

### Dimensions:
* Programming Language?
* Domain (i.e. slicing by "dataframes" provides results for Pandas etc)?

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
![Data Engineering Stack](./src/main/resources/de_stack.png)

## Data

### 
The primary source fo data for the github commits is pulled from mongodb dumps converted to json:

    root
     |-- _id: string (nullable = true)
     |-- author: struct (nullable = true)
     |    |-- avatar_url: string (nullable = true)
     |    |-- events_url: string (nullable = true)
     |    |-- followers_url: string (nullable = true)
     |    |-- following_url: string (nullable = true)
     |    |-- gists_url: string (nullable = true)
     |    |-- gravatar_id: string (nullable = true)
     |    |-- html_url: string (nullable = true)
     |    |-- id: long (nullable = true)
     |    |-- login: string (nullable = true)
     |    |-- organizations_url: string (nullable = true)
     |    |-- received_events_url: string (nullable = true)
     |    |-- repos_url: string (nullable = true)
     |    |-- site_admin: boolean (nullable = true)
     |    |-- starred_url: string (nullable = true)
     |    |-- subscriptions_url: string (nullable = true)
     |    |-- type: string (nullable = true)
     |    |-- url: string (nullable = true)
     |-- comments_url: string (nullable = true)
     |-- commit: struct (nullable = true)
     |    |-- author: struct (nullable = true)
     |    |    |-- date: string (nullable = true)
     |    |    |-- email: string (nullable = true)
     |    |    |-- name: string (nullable = true)
     |    |-- comment_count: long (nullable = true)
     |    |-- committer: struct (nullable = true)
     |    |    |-- date: string (nullable = true)
     |    |    |-- email: string (nullable = true)
     |    |    |-- name: string (nullable = true)
     |    |-- message: string (nullable = true)
     |    |-- tree: struct (nullable = true)
     |    |    |-- sha: string (nullable = true)
     |    |    |-- url: string (nullable = true)
     |    |-- url: string (nullable = true)
     |    |-- verification: struct (nullable = true)
     |    |    |-- payload: string (nullable = true)
     |    |    |-- reason: string (nullable = true)
     |    |    |-- signature: string (nullable = true)
     |    |    |-- verified: boolean (nullable = true)
     |-- committer: struct (nullable = true)
     |    |-- avatar_url: string (nullable = true)
     |    |-- events_url: string (nullable = true)
     |    |-- followers_url: string (nullable = true)
     |    |-- following_url: string (nullable = true)
     |    |-- gists_url: string (nullable = true)
     |    |-- gravatar_id: string (nullable = true)
     |    |-- html_url: string (nullable = true)
     |    |-- id: long (nullable = true)
     |    |-- login: string (nullable = true)
     |    |-- organizations_url: string (nullable = true)
     |    |-- received_events_url: string (nullable = true)
     |    |-- repos_url: string (nullable = true)
     |    |-- site_admin: boolean (nullable = true)
     |    |-- starred_url: string (nullable = true)
     |    |-- subscriptions_url: string (nullable = true)
     |    |-- type: string (nullable = true)
     |    |-- url: string (nullable = true)
     |-- files: array (nullable = true)
     |    |-- element: struct (containsNull = true)
     |    |    |-- additions: long (nullable = true)
     |    |    |-- blob_url: string (nullable = true)
     |    |    |-- changes: long (nullable = true)
     |    |    |-- contents_url: string (nullable = true)
     |    |    |-- deletions: long (nullable = true)
     |    |    |-- filename: string (nullable = true)
     |    |    |-- raw_url: string (nullable = true)
     |    |    |-- sha: string (nullable = true)
     |    |    |-- status: string (nullable = true)
     |-- html_url: string (nullable = true)
     |-- parents: array (nullable = true)
     |    |-- element: struct (containsNull = true)
     |    |    |-- html_url: string (nullable = true)
     |    |    |-- sha: string (nullable = true)
     |    |    |-- url: string (nullable = true)
     |-- sha: string (nullable = true)
     |-- stats: struct (nullable = true)
     |    |-- additions: long (nullable = true)
     |    |-- deletions: long (nullable = true)
     |    |-- total: long (nullable = true)
     |-- url: string (nullable = true)

