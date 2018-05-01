
USE insight;

DROP TABLE IF EXISTS GitHubData ;

CREATE TABLE GitHubData (
	date DATE,
    language_name VARCHAR(32),
    package_name VARCHAR(255),
    usage_count INT
    )
;

SELECT * FROM GitHubData;

select count(1) from GitHubData;