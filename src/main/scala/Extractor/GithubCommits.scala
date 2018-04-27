package Extractor
//import org.apache.spark
//import org.apache.spark
// import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import Extractor.GithubCommitExtractor


object GithubCommits{
  // This is a dumb starting point to attempt to get spark to read some json commits
  def main(): Unit = { //args: Array[String]

    val spark = SparkSession
                .builder()
                .appName("Parsing GithubCommits")
                .getOrCreate()

    val sc = spark.sparkContext

    val testFile = sc.textFile("/home/eric/Insight/testing_data/github_test_1000.json")

    val whatever = sc.parallelize(1 to 3).filter(_ )


  }



  def parseCommit(rawCommit: String): (String, String) = {

    GithubCommitExtractor.extractCommitMetaData(rawCommit)
  }

}

