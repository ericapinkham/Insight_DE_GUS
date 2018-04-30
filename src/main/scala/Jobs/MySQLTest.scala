package Jobs

//import org.apache.spark
//import org.apache.spark.{SparkConf, SparkContext}
//import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.SparkSession
//import Extractor.MySQLProperties


object MySQLTest extends MySQLConnection {
  def main(args: Array[String]) {
    // Spark
    
    val sparkSession = SparkSession
      .builder()
      .appName("Spark SQL")
      .getOrCreate()
    
    val sc = sparkSession.sparkContext
  
    // MySQL
    val table = "GithubData"
    val data = sparkSession.read.jdbc(connectionString, table, jdbcProperties)
    
    data.printSchema()
  }
}