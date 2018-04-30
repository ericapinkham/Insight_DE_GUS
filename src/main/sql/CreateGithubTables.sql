


DROP TABLE IF EXISTS GitHubData ;

CREATE TABLE GitHubData (
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

SELECT 	CAST(commit_timestamp AS DATE) AS date,
		language_name,
        package_name,
        SUM(usage_count) AS total_usage
	FROM GitHubData
    GROUP BY 1, 2, 3
    ORDER BY 4 DESC
;

