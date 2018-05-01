package Jobs

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}
import Jobs.Extractor.{RepoRecord, UserRecord}
import Jobs.Extractor.CommitRecord.extractCommit
import org.apache.spark.sql.functions.to_timestamp

object ExtractGitHubData extends MySQLConnection {
  /**
    *
    * @param args: full file path
    */
  def main(args: Array[String]): Unit = {
    // Extract the arguments
    val dataDirectory: String = args match {
      case Array(dir) => dir
      case _ => throw new Error("Invalid arguments passed")
    }
    
    // setup the spark context
    val conf = new SparkConf().setAppName("Parsing GitHub Commits")
    val sc = new SparkContext(conf)

    // SparkSQL
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._

    // Read text files into spark RDD, map to objects and convert to DF
    val commitsDf = sc.textFile(s"$dataDirectory/commits.json").flatMap{s => extractCommit(s)}.toDF

    val usersDf = sc.textFile(s"$dataDirectory/users.json").map(s => UserRecord(s)).toDF

//    val reposDf = sc.textFile(s"$dataDirectory/repos.json").map(s => RepoRecord(s)).toDF

    val resultDf = commitsDf.join(usersDf, Seq("id"), "left_outer")

    resultDf.write.parquet(s"$dataDirectory/github_data.parquet")
    resultDf.printSchema()
//    resultDf.show()
//    val ts = to_timestamp($"commit_timestamp", "yyyy-MM-dd'T'hh:mm:ss'Z'") // for mapping timestamps so that MySQL can deal
//    commits.withColumn("commit_timestamp", ts).write.mode(SaveMode.Append).jdbc(connectionString, "GitHubData", jdbcProperties)
  }
}

