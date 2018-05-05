package Jobs

import org.apache.spark.sql.SparkSession

object CockroachDBTest {
  def main(args: Array[String]): Unit = {
    // Extract the arguments
    val dataDirectory: String = args match {
      case Array(dir) => dir
      case _ => throw new Error("Invalid arguments passed")
    }
  
  
    val spark = SparkSession.builder().getOrCreate()
  
    // CockroachDB
    val host: String = "ec2-52-36-220-17.us-west-2.compute.amazonaws.com"
    val port: Int = 26257
    val database: String = "insight"
    val user: String = "maxroach"
    val password: String = ""
  
    // Build the inherited members
    val connectionString = s"jdbc:postgresql://$host:$port/$database?sslmode=disable"
    val jdbcProperties = new java.util.Properties
    jdbcProperties.setProperty("driver", "org.postgresql.Driver")
    jdbcProperties.setProperty("user", s"$user")
    jdbcProperties.setProperty("password", s"$password")
    
    
    val testDf = spark.read.jdbc(connectionString, "test", jdbcProperties)
    testDf.printSchema()
  
  }

  
}
