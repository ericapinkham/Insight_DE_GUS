package Extractor

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}
import Extractor.GitHubCommitExtractor.extract
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
    val conf = new SparkConf().setMaster("local").setAppName("Dataframe")
    val sc = new SparkContext(conf)
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._
    
//	  val sc = SparkContext.getOrCreate()
    
    // Read text file into spark RDD
    val commitsRDD = sc.textFile(fileName).flatMap{ s => extract(s)}.toDF()
    
    val ts = to_timestamp($"commit_timestamp", "yyyy-MM-dd'T'hh:mm:ss'Z'")
    commitsRDD.withColumn("commit_timestamp", ts).write.mode(SaveMode.Append).jdbc(connectionString, "GitHubData", jdbcProperties)
    
    // how to store split values in different column and write it into file
  }
}

