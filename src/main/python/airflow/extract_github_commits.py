#!/usr/bin/python3

from airflow import DAG
from airflow.operators.bash_operator import BashOperator
from airflow.operators.python_operator import PythonOperator
from airflow.operators.sensors import HttpSensor
from airflow.operators.postgres_operator import PostgresOperator
from datetime import datetime, timedelta

default_args = {
    'owner': 'eric',
    'depends_on_past': True,
    'start_date': datetime(2018, 4, 1),
    'email': ['eric.a.pinkham+airflow@gmail.com'],
    'email_on_failure': False,
    'email_on_retry': False,
    'retries': 5,
    'retry_delay': timedelta(minutes = 5)
    # 'concurrency': 4
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
  --executor-memory 6G \
  {{ params.hdfs_dir }}/code/insight-assembly-1.0.jar \
  {{ ds }}
    """,
    params = {'hdfs_dir': hdfs_dir},
    dag = dag
)

# build a daily summary
cockroachdb_daily_import_summary = PostgresOperator(
    task_id = 'cockroachdb_daily_import_summary',
    postgres_conn_id = 'cockroachdb',
    autocommit = True,
    sql = """
    WITH import_summary AS (
    	SELECT	commit_date,
    			language_name,
    			import_name,
    			usage_count,
    			ROW_NUMBER() OVER (PARTITION BY language_name ORDER BY usage_count DESC) AS row_number
    		FROM commits
    		WHERE commit_date = '{{ ds }}'
    	)
    UPSERT INTO daily_import_summary (
    	summary_date,
    	language_name,
    	import_name,
    	usage_count
    )
    SELECT  commit_date,
    		language_name,
    		import_name,
    		usage_count
    	FROM import_summary
    	WHERE row_number <= 5000
    ;""",
    dag = dag
)

# Get daily language totals
cockroachdb_daily_language_totals = PostgresOperator(
    task_id = 'cockroachdb_daily_language_totals',
    postgres_conn_id = 'cockroachdb',
    autocommit = True,
    sql = """
    UPSERT INTO daily_language_totals (
    	language_name,
    	commit_date,
    	total_daily_usage
    )
    SELECT language_name,
    		commit_date,
    		CAST(SUM(usage_count) AS INT) AS total_daily_usage
    	FROM commits
    	WHERE commit_date = '{{ ds }}'
    	GROUP BY commit_date,
    		language_name
    ;""",
    dag = dag
)

# define the DAG edges
download.set_upstream(check_for_new_dump)
bsondump.set_upstream(download)
remove_staging_file.set_upstream(bsondump)
spark_parse_commits.set_upstream(remove_staging_file)
cockroachdb_daily_import_summary.set_upstream(spark_parse_commits)
cockroachdb_daily_language_totals.set_upstream(spark_parse_commits)
