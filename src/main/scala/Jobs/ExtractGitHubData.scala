package Jobs

import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}
import Jobs.Extractor.CommitRecord.extractCommit
import org.apache.spark.sql.functions.to_date

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

    val toDate = to_date($"commit_timestamp", "yyyy-MM-dd'T'hh:mm:ss'Z'")
    val commitsDf = sc.textFile(s"$dataDirectory/commits.json")
      .flatMap{s => extractCommit(s)}
      .toDF()
      .withColumn("commit_timestamp", toDate)




    //    val reposDf = sc.textFile(s"$dataDirectory/repos.json").map(s => RepoRecord(s)).toDF

//    val resultDf =
//    resultDf.write.parquet(s"$dataDirectory/github_data.parquet")
//    resultDf.show()
//    val ts = to_timestamp($"commit_timestamp", "yyyy-MM-dd'T'hh:mm:ss'Z'") // for mapping timestamps so that MySQL can deal
//    commits.withColumn("commit_timestamp", ts).write.mode(SaveMode.Append).jdbc(connectionString, "GitHubData", jdbcProperties)
  }
}

