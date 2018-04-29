package Extractor
import org.apache.spark.SparkContext
import Extractor.GithubCommitExtractor.parseMetaData


import scala.io.Source.fromFile

object GithubCommits {

  def main(args: Array[String]): Unit = {
    // Extract the arguments
    val fileName: String = args match {
      case Array(file) => file
      case _ => throw new Error("Invalid arguments passed")
    }
    
    // setup the spark context
	  val sc = SparkContext.getOrCreate()
    

    // Read text file into spark RDD
    val commitsRDD = sc.textFile(fileName)
    
    // Extract meta data
    val splitRdd = commitsRDD.map { s => parseMetaData(s)}

    // printing values
    splitRdd.foreach(println)

    // how to store split values in different column and write it into file
  }
}

