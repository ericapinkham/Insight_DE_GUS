package Jobs

import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.spark.{SparkConf, SparkContext}
import Jobs.Extractor.UserRecord
import org.apache.spark.sql.functions.to_date

object ExtractStraightToSQL extends MySQLConnection {
  /**
    *
    * @param args: full file path
    */
  def main(args: Array[String]): Unit = {
    // Extract the arguments
    val dataDirectory: String = args match {
      case Array(dir) => dir
      case _ => throw new Error("Invalid arguments passed")
    }

    // setup the spark context
    val conf = new SparkConf().setAppName("Parsing GitHub Commits")
    val sc = new SparkContext(conf)

    // SparkSQL
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._

    // Read text files into spark RDD, map to objects and convert to DF


    val usersDf = sc.textFile(s"$dataDirectory/users.json")
      .map(s => UserRecord(s))
      .filter(_.id != 0)
      .keyBy(_.id)
      .reduceByKey{_ + _}
      .map(_._2)
      .toDF()

    usersDf.printSchema()

    val ts = to_date($"created_at", "yyyy-MM-dd'T'hh:mm:ss'Z'") // for mapping timestamps so that MySQL can deal .withColumn("created_at", ts)
    usersDf.withColumn("created_at", ts).write.mode(SaveMode.Append).jdbc(connectionString, "UserData", jdbcProperties)
  }
}

