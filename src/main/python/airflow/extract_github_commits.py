#!/usr/bin/python3

from airflow import DAG
from airflow.operators.bash_operator import BashOperator
from airflow.operators.python_operator import PythonOperator
from airflow.operators.sensors import HttpSensor
import psycopg2
from datetime import datetime, timedelta

default_args = {
    'owner': 'eric',
    'depends_on_past': True,
    'start_date': datetime(2018, 5, 1),
    'email': ['eric.a.pinkham+airflow@gmail.com'],
    'email_on_failure': False,
    'email_on_retry': False,
    'retries': 1,
    'retry_delay': timedelta(minutes=5),
}

dag = DAG('extract_github_commits', default_args = default_args, schedule_interval = '@daily')
hdfs_dir = 'hdfs://10.0.0.9:9000'

# Check if the new dump exists yet, retry every hour until it does
check_for_new_dump = HttpSensor(
    task_id = 'check_for_new_dump',
    http_conn_id = 'ghtorrent',
    method = 'HEAD',
    poke_interval = 60 * 60,
    timeout = 60 * 60 * 24,
    endpoint = """mongo-dump-{{ ds }}.tar.gz""",
    dag = dag
)

# Download the bson file
download = BashOperator(
    task_id = 'download',
    bash_command = """
wget -qO- http://ghtorrent-downloads.ewi.tudelft.nl/mongo-daily/mongo-dump-{{ ds }}.tar.gz | tar xvz dump/github/commits.bson --strip-components=2
wait
mv commits.bson ~/staging/commits_{{ ds }}.bson
    """,
    params = {'hdfs_dir': hdfs_dir},
    dag = dag
)

# this extracts the bson file
bsondump = BashOperator(
    task_id = 'bsondump',
    bash_command = """bsondump ~/staging/commits_{{ ds }}.bson | hdfs dfs -appendToFile - {{ params.hdfs_dir }}/data/commits_{{ ds }}.json""",
    params = {'hdfs_dir': hdfs_dir},
    dag = dag
)

# remove the bson
remove_staging_file = BashOperator(
    task_id = 'remove_staging_file',
    bash_command = """rm ~/staging/commits_{{ ds }}.bson""",
    dag = dag
)

# this is runs the spark job
spark_parse_commits = BashOperator(
    task_id = 'spark_parse_commits',
    bash_command = """
spark-submit \
  --class Jobs.ExtractGitHubData \
  --master spark://10.0.0.9:7077 \
  --executor-memory 3G \
  {{ params.hdfs_dir }}/code/insight-assembly-0.4.jar \
  {{ ds }}
    """,
    params = {'hdfs_dir': hdfs_dir},
    dag = dag
)

# An interface to cockroachdb
def execute_sql(command):
    # Setup the connection
    connection = psycopg2.connect(database='insight', user='maxroach', host='ec2-52-36-220-17.us-west-2.compute.amazonaws.com', port=26257)
    connection.set_session(autocommit = True)
    cur = connection.cursor()

    # Execute the command server side
    try:
        cur.execute(command)
    except:
        raise ValueError('Error executing commit update')
    # Close the cursor and connection
    cur.close()
    connection.close()

# Prep the command to update commits
insert_commits_command = """
INSERT INTO commits
	SELECT commit_date,
		language_name,
		import_name,
		usage_count
		FROM received_commits
		WHERE received_date = '{{ ds }}'
		ON CONFLICT (commit_date, language_name, import_name) DO UPDATE SET usage_count = commits.usage_count + excluded.usage_count
;"""
cockroachdb_insert_commits = PythonOperator(
    task_id = 'cockroachdb_insert_commits',
    python_callable = execute_sql,
    op_kwargs={'command': insert_commits_command},
    dag=dag
    )

download.set_upstream(check_for_new_dump)
bsondump.set_upstream(download)
remove_staging_file.set_upstream(bsondump)
spark_parse_commits.set_upstream(remove_staging_file)
cockroachdb_insert_commits.set_upstream(spark_parse_commits)
