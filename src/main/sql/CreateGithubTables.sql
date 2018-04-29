


DROP TABLE IF EXISTS GithubData ;

CREATE TABLE GithubData (
	commit_timestamp DATETIME,
    user_email VARCHAR(255),
    commit_message TEXT,
    file_name TEXT,
    -- patch TEXT, 
    language_name VARCHAR(32),
    package_name VARCHAR(255),
    usage_count INT
    )
;

