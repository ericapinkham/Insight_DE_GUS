package Extractor
import org.apache.spark.sql.SparkSession
import Extractor.GithubCommitExtractor.extractCommitMetaData

object GithubCommits {

  def main(args: Array[String]): Unit = {
    // Get or create spark and sc
    val spark = SparkSession
      .builder()
      .appName("Parsing GithubCommits")
      .getOrCreate()

    val sc = spark.sparkContext

    // Read text file in spark RDD
    val extractedBsonCommits = sc.textFile("/home/eric/Insight/testing_data/github_test_1000.json")

    val splitRdd = extractedBsonCommits.map { s => extractCommitMetaData(s)}

    // printing values
    splitRdd.foreach(println)

    // how to store split values in different column and write it into file
  }






}

