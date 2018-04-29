spark-submit --jars /usr/share/java/mysql-connector-java-5.1.38.jar \
--class Extractor.GithubCommits \
/home/eric/Insight/PackageTracker/target/scala-2.11/insight_data_engineering-assembly-0.1.jar \
/home/eric/Insight/testing_data/github_test_100.json
