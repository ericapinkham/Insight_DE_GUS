package Jobs

import Jobs.Extractor.CommitRecord
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}
import Jobs.Extractor.CommitRecord.extractCommit

object ExtractGitHubData extends DBConnection {
  /**
    * @param args: full file path
    */
  def main(args: Array[String]): Unit = {
    // Extract the arguments
    val dataDirectory: String = args match {
      case Array(dir) => dir
      case _ => throw new Error("Invalid arguments passed")
    }

    // The date
    val today = "2018-05-03"

    // setup the spark context
    val conf = new SparkConf().setAppName("Parsing GitHub Commits")
    val sc = new SparkContext(conf)

    // SparkSQL
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._

    // Read text files into spark RDD, map to objects and convert to DF
    val commitsDf = sc.textFile(s"$dataDirectory/commits.json")
      .flatMap{s => extractCommit(s)}
      .repartition(500)(Ordering[CommitRecord])
      .toDF()
      .groupBy("commit_date", "language_name", "import_name")
      .sum("usage_count")
      .withColumnRenamed("sum(usage_count)", "usage_count")

    commitsDf
      .write
      .mode(SaveMode.Append)
      .jdbc(connectionString, "github_commits", jdbcProperties)

    // Update imports
    val importsDf = spark.read.jdbc(connectionString, "imports", jdbcProperties)

    commitsDf.select("language_name", "import_name").distinct().createOrReplaceTempView("commits")
    importsDf.createOrReplaceTempView("imports")
    spark.sql("""
        |SELECT c.language_name,
        |       c.import_name
        | FROM commits c
        | LEFT JOIN imports i
        |   ON i.language_name = c.language_name
        |   AND i.import_name = c.import_name
        |   WHERE i.import_name IS NULL
      """.stripMargin)
      .write
      .mode(SaveMode.Append)
      .jdbc(connectionString, "imports", jdbcProperties)

//    // Import Summary
//    val window = Window.partitionBy("language_name").orderBy($"usage_count".desc)
//    commitsDf.filter(s"commit_date = '$today'")
//      .withColumn("r", row_number.over(window))
//      .where($"r" <= 10)
//      .select("commit_date", "language_name", "import_name", "usage_count")
//      .withColumnRenamed("commit_date", "date")
//      .write
//      .mode(SaveMode.Append)
//      .jdbc(connectionString, "import_summary", jdbcProperties)
  }
}

