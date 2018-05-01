package Jobs

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}
import Jobs.Extractor.CommitRecord
import org.apache.spark.sql.functions.to_timestamp

object GitHubCommits extends MySQLConnection {
  /**
    *
    * @param args: full file path
    */
  def main(args: Array[String]): Unit = {
    // Extract the arguments
    val fileName: String = args match {
      case Array(file) => file
      case _ => throw new Error("Invalid arguments passed")
    }
    
    // setup the spark context
    val conf = new SparkConf().setAppName("Parsing GitHub Commits")
    val sc = new SparkContext(conf)

    // SparkSQL
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._

    // Read text file into spark RDD, map to Commit objects and convert to DF
    val commitsRDD = sc.textFile(fileName)
      .flatMap{s => CommitRecord(s)}.toDF()
    
    val ts = to_timestamp($"commit_timestamp", "yyyy-MM-dd'T'hh:mm:ss'Z'") // for mapping timestamps so that MySQL can deal
    commitsRDD.withColumn("commit_timestamp", ts).write.mode(SaveMode.Append).jdbc(connectionString, "GitHubData", jdbcProperties)
  }
}

