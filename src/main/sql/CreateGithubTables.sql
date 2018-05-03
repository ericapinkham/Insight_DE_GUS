
USE insight;

DROP TABLE IF EXISTS GitHubData ;

CREATE TABLE GitHubData (
	date DATE,
    language_name VARCHAR(32),
    package_name VARCHAR(255),
    usage_count INT
    )
;

SELECT * FROM GitHubData order by usage_count desc;

select count(1) from GitHubData;


 SELECT  package_name package,
            SUM(usage_count) Inclusions
        FROM GitHubData
		WHERE language_name = 'scala'
			-- AND date BETWEEN '2018-01-01' AND '2018-05-24'
        GROUP BY package_name
        ORDER BY 2 DESC
        LIMIT 10;
	

insert into GitHubData
	select * from GitHubData
;
        
SELECT 	package_name, 
        DATE_ADD(CURDATE(), INTERVAL -1 *(FLOOR( 1 + RAND( ) *60 )) DAY)
	FROM GitHubData
	LIMIT 30
;

update GitHubData set date = DATE_ADD(CURDATE(), INTERVAL -1 *(FLOOR( 1 + RAND( ) *60 )) DAY);

drop table if exists UserData;

create table UserData(
	id int,
    created_at date,
    location varchar(255),
    email varchar(255),
    login varchar(255)
	)
    ;
    
    
select * from UserData;

select 	count(1),
		count(distinct id)
	from UserData
    where id != 0
;

select count(1) from UserData where Location Is not null;