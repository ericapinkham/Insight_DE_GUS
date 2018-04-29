package Extractor

//import org.apache.spark
//import org.apache.spark.{SparkConf, SparkContext}
//import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.SparkSession
//import Extractor.MySQLProperties


object MySQLTest extends MySQLConnection {
  def main() {
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

//def main() {
//  // Spark
//  val sparkSession = SparkSession
//    .builder()
//    .appName("Spark SQL with UDF")
//    .getOrCreate()
//
//  val sc = sparkSession.sparkContext
//
//  // MySQL
//  val jdbcProperties = new java.util.Properties
//  jdbcProperties.setProperty("driver", "com.mysql.jdbc.Driver")
//  jdbcProperties.setProperty("user", "root")
//  jdbcProperties.setProperty("password", "Galois2Extension")
//
//  val url = "jdbc:mysql://ec2-35-161-183-67.us-west-2.compute.amazonaws.com:3306/insight"
//  val table = "GithubData"
//  val data = sparkSession.read.jdbc(url, table, jdbcProperties)
//
//  data.printSchema()
//}