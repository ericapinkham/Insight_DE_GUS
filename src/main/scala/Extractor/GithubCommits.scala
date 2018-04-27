package Extractor
import org.apache.spark.sql.SparkSession
import Extractor.GithubCommitExtractor.extractCommitMetaData
import scala.io.Source.fromFile

object GithubCommits {

  def main(args: Array[String]): Unit = { //



    // Get or create spark and sc
    val spark = SparkSession
      .builder()
      .appName("Parsing GithubCommits")
      .getOrCreate()

    val sc = spark.sparkContext

    // try to talk to mysql
    val host: String = "35.161.183.67"
    val port: Int = 3306
    val database: String = "insight"
    val table: String = "raw_dumps"
    val user: String = "root"
    val password: String = fromFile("/home/eric/Insight/insight_data_engineering/src/main/resources/mysqlPassword.txt")
      .getLines().toList.head
    val options = Map(
      "url" -> s"jdbc:mysql://$host:$port/$database?zeroDateTimeBehavior=convertToNull",
      "dbtable" -> table,
      "user" -> user,
      "password" -> password)

    val df = spark.read.format("jdbc").options(options).load()
    df.printSchema()

    // Read text file in spark RDD
    val extractedBsonCommits = sc.textFile("hdfs://10.0.0.14:9000/hdfs/github_test_100.json")

    val splitRdd = extractedBsonCommits.map { s => extractCommitMetaData(s)}

    // printing values
    splitRdd.foreach(println)

    // how to store split values in different column and write it into file
  }
}

