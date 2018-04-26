//package GithubCommits
//import org.apache.spark
//import org.apache.spark
//import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

object GithubCommits{
  // This is a dumb starting point to attempt to get spark to read some json commits
  def main(): Unit = {
    val spark = SparkSession
      .builder()
      .appName("Spark SQL with UDF")
      .getOrCreate()

    // Creates a DataFrame from json file
    val dataSet = 2
//    val df = spark.read.json("/home/eric/Insight/testing_data/super_simple.json")
    val df = spark.read.json(s"/home/eric/Insight/testing_data/github_test_$dataSet.json")

    // Look at the schema of this DataFrame for debugging.
    df.show()
    df.printSchema()

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
