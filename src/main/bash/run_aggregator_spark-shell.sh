#!/usr/bin/env bash
spark-submit \
--class Jobs.AggregateGitHubData \
/home/eric/Insight/insight_data_engineering/target/scala-2.11/insight_data_engineering-assembly-0.2.jar \
/home/eric/Insight/testing_data/20180429
