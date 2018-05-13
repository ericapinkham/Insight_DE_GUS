package Jobs

import Jobs.Extractor.CommitRecord
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.storage.StorageLevel.MEMORY_ONLY_SER
import Jobs.Extractor.CommitRecord.extractCommit

/**
  * Run the spark job
  */
object ExtractGitHubData extends DBConnection {
  /**
    * The job spark will run
    * @param args the date in YYYY-MM-DD format
    */
  def main(args: Array[String]): Unit = {
    // Extract the arguments
    val loadDate: String = args match {
      case Array(date) => date
      case _ => throw new Error("Invalid arguments passed")
    }

    // setup the spark context
    val conf = new SparkConf().setAppName("Parsing GitHub Commits")
    val sc = new SparkContext(conf)

    // SparkSQL
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._

    // Read text files into spark RDD, map to objects and convert to DF
    val commitsDf = sc.textFile(s"hdfs://10.0.0.9:9000/data/commits_$loadDate.json")
      .persist(MEMORY_ONLY_SER)
      .flatMap{s => extractCommit(s, loadDate)}
      .repartition(500)(Ordering[CommitRecord])
      .toDF()
      .groupBy("commit_date", "language_name", "import_name")
      .sum("usage_count")
      .withColumnRenamed("sum(usage_count)", "usage_count")

    commitsDf
      .write
      .mode(SaveMode.Append)
      .jdbc(connectionString, "commits", jdbcProperties)

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
  }
}

