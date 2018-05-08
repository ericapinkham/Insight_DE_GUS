
USE insight;

DROP TABLE IF EXISTS github_commits ;

CREATE TABLE github_commits (
	commit_date DATE,
	language_name VARCHAR(32),
	package_name VARCHAR(255),
	usage_count INT
  )
;
