
USE insight;

DROP TABLE IF EXISTS github_commits ;

WITH TOPTEN AS (
    SELECT	*,
			ROW_NUMBER() OVER (PARTITION BY language_name ORDER BY usage_count DESC) AS RowNo
    	FROM github_commits
		WHERE commit_date = '2018-05-03'
	)
INSERT INTO import_summary
	SELECT 	'2018-05-03',
			language_name,
			import_name,
			usage_count
		FROM TOPTEN
		WHERE RowNo <= 10
