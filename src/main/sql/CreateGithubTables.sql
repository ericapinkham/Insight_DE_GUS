


DROP TABLE IF EXISTS GithubData ;

CREATE TABLE GithubData (
	commit_timestamp DATETIME NOT NULL,
    user_email VARCHAR(255),
    commit_message TEXT,
    file_name VARCHAR(32) NOT NULL,
    -- patch TEXT, 
    language_name VARCHAR(32) NOT NULL,
    package_name VARCHAR(32) NOT NULL,
    usage_count INT NOT NULL DEFAULT 1
    )
;



