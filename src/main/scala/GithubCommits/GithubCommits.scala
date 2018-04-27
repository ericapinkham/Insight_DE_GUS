//package GithubCommits
//import org.apache.spark
//import org.apache.spark
// import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.explode
//import org.apache.hadoop.fs.s3a.S3AFileSystem
import scala.util.parsing.json

object GithubCommits{
  // This is a dumb starting point to attempt to get spark to read some json commits
  def main(): Unit = { //args: Array[String]

    val spark = SparkSession
                .builder()
                .appName("Parsing GithubCommits")
                .getOrCreate()

    val sc = spark.sparkContext

    val testFile = sc.textFile("/home/eric/Insight/testing_data/github_test_1000.json")

    val asd = for (line <- testFile) {
      println(json.JSON.parseFull(line))
    }

    val whatever = sc.parallelize(1 to 3).filter(_ )

//    println(asd.count())
//
//    val df = spark.read.json("/home/eric/Insight/testing_data/github_test_1000.json")
//
//    df.createOrReplaceTempView("Commits")
//    val additions = spark.sql("SELECT files.element.additions from Commits")
////    sc.parallelize(1 to 3).filter()
////    df.select(explod)
////    val additions = df.select(explode(df("files.element.additions")))
////    queryResult.write.csv("./test.csv")
//
//    for (line <- additions) println(line)

//    df.temp

//    df.printSchema()

  }
}



//
//// For implicit conversions like converting RDDs to DataFrames
////import spark.implicits._
//
//object SparkGroupBy {
//  def main(args: Array[String]) {
//    val spark = SparkSession
//      .builder()
//      .appName("Spark SQL with UDF")
//      .getOrCreate()
//
//    // Creates a DataFrame from json file
//    val df = spark.read.json("baby.json")
//
//    // Look at the schema of this DataFrame for debugging.
//    df.printSchema()
//
//    // Counts people by age
//    val countsByAsin = df.groupBy("asin").count()
//
//    // Saves countsByAge to hdfs in the JSON format.
//    countsByAsin.write.format("json").save("results.json")
//  }
//}
//
