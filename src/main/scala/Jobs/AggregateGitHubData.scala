package Jobs

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{SparkSession, SaveMode}
import org.apache.spark.sql.functions.to_date

object AggregateGitHubData extends DBConnection {
  def main(args: Array[String]): Unit = {
    // Extract the arguments
    val dataDirectory: String = args match {
      case Array(dir) => dir
      case _ => throw new Error("Invalid arguments passed")
    }

    // setup the spark context
    val conf = new SparkConf().setAppName("Aggregating GitHub Commits")
    val sc = new SparkContext(conf)

    // SparkSQL
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._

    // Read text files into spark RDD, map to objects and convert to DF
    val toDate = to_date($"commit_timestamp", "yyyy-MM-dd'T'hh:mm:ss'Z'")
    val githubDf = spark.sqlContext.read.parquet(s"$dataDirectory/github_data.parquet").withColumn("commit_timestamp", toDate)

//    root
//    |-- id: integer (nullable = false)
//    |-- commit_timestamp: string (nullable = true)
//    |-- user_email: string (nullable = true)
//    |-- commit_message: string (nullable = true)
//    |-- file_name: string (nullable = true)
//    |-- language_name: string (nullable = true)
//    |-- package_name: string (nullable = true)
//    |-- usage_count: integer (nullable = false)
//    |-- created_at: string (nullable = true)
//    |-- location: string (nullable = true)

    val aggregatedDf =
      githubDf.groupBy("commit_timestamp", "language_name", "package_name")
        .sum("usage_count")
        .withColumnRenamed("SUM(usage_count)", "usage_count")
        .withColumnRenamed("commit_timestamp", "date")

    aggregatedDf.printSchema()

     // for mapping timestamps so that MySQL can deal
    aggregatedDf.write.mode(SaveMode.Append).jdbc(connectionString, "GitHubData", jdbcProperties)
  }

}
