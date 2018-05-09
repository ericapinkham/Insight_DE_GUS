
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

UPDATE c
	SET c.usage_count = c.usage_count + r.usage_count
	FROM commits c
	INNER JOIN received_commits r
		ON r.received_date = 'date'
		AND c.commit_date = r.commit_date
		AND c.language_name = r.language_name
		AND c.import_name = r.import_name
	;

-- Insert new records into commits
INSERT INTO commits
	SELECT r.commit_date,
		r.language_name,
		r.import_name,
		r.usage_count
		FROM received_commits r
		LEFT JOIN commits c
			ON c.commit_date = r.commit_date
			AND c.language_name = r.language_name
			AND c.import_name = r.import_name
		WHERE r.received_date = 'date'
	;
