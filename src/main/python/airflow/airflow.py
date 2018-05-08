#!/usr/bin/python3

from airflow import DAG # The DAG object; we'll need this to instantiate a DAG
from airflow.operators.bash_operator import BashOperator # Operators; we need this to operate!

from datetime import datetime, timedelta

default_args = {
    'owner': 'airflow',
    'depends_on_past': False,
    'start_date': datetime(2015, 6, 1),
    'email': ['eric.a.pinkham+airflow@gmail.com'],
    'email_on_failure': False,
    'email_on_retry': False,
    'retries': 1,
    'retry_delay': timedelta(minutes=5),
    # 'queue': 'bash_queue',
    # 'pool': 'backfill',
    # 'priority_weight': 10,
    # 'end_date': datetime(2016, 1, 1),
}

dag = DAG('tutorial', default_args = default_args, schedule_interval = timedelta(1))

date = '2018-05-07'

download = BashOperator(
    task_id = 'download and extract dump',
    bash_command = """wget -qO- http://ghtorrent-downloads.ewi.tudelft.nl/mongo-daily/mongo-dump-{{ date }}.tar.gz | tar xvz dump/github/commits.bson""",
    params = {'date': date},
    dag = dag
)

# Put the extracted file in hdfs
bsondump = BashOperator(
    task_id = 'bsondump to hdfs'
    bash_command = """bsondump /home/ubuntu/dump/github/commits.bson | hdfs dfs -appendToFile - hdfs://10.0.0.9:9000/data/commits_{{ date }}.json"""
    params = {'date': date}
    dag = dag
)

t3 = BashOperator(
    task_id = 'extract to cockroachdb'
    bash_command = """
spark-submit \
  --class Jobs.ExtractGitHubData \
  --master spark://10.0.0.9:7077 \
  --executor-memory 3G \
  hdfs://10.0.0.9:9000/code/insight-assembly-0.4.jar \
  hdfs://10.0.0.9:9000/data/commits_{{ date }}.json
    """,
    params = {'date': date}
    dag = dag
)

t2.set_upstream(t1)
t3.set_upstream(t1)
