
USE insight;

-- received_commits
DROP TABLE IF EXISTS received_commits ;
CREATE TABLE received_commits (
	received_date DATE,
	commit_date DATE,
	language_name VARCHAR(64),
	import_name VARCHAR(1024),
	usage_count INT
  )
;
CREATE INDEX ON received_commits (received_date, commit_date, language_name, import_name) STORING (usage_count);

-- commits
DROP TABLE IF EXISTS commits ;
CREATE TABLE commits (
	commit_date DATE,
	language_name VARCHAR(64),
	import_name VARCHAR(1024),
	usage_count INT,
	PRIMARY KEY (commit_date, language_name, import_name)
	)
;

-- Update commits
INSERT INTO commits
	SELECT commit_date,
		language_name,
		import_name,
		usage_count
		FROM received_commits
		WHERE received_date = 'date'
		ON CONFLICT (commit_date, language_name, import_name) DO UPDATE SET usage_count = commits.usage_count + excluded.usage_count
;

CREATE TABLE daily_import_summary (
	summary_date DATE,
	language_name STRING(64),
	import_name	STRING(1024),
	usage_count INT,
	PRIMARY KEY(summary_date, language_name, import_name)
)
;

WITH import_summary AS (
	SELECT	commit_date,
			language_name,
			import_name,
			usage_count,
			ROW_NUMBER() OVER (PARTITION BY language_name ORDER BY usage_count DESC) AS row_number
		FROM commits
		WHERE commit_date = '2018-05-07'
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
	WHERE row_number <= 10
	ORDER BY usage_count DESC
;

CREATE TABLE daily_language_totals (
	language_name STRING(64),
	commit_date DATE,
	total_daily_usage INT,
	PRIMARY KEY (language_name, commit_date)
)
;

UPSERT INTO daily_language_totals (
	language_name,
	commit_date,
	total_daily_usage
)
SELECT language_name,
		commit_date,
		CAST(SUM(usage_count) AS INT) AS total_daily_usage
	FROM received_commits
	WHERE received_date = '2018-05-07'
	GROUP BY commit_date,
		language_name
;

CREATE TABLE _change
