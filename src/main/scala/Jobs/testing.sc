import org.apache.spark.{RangePartitioner, SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

// setup the spark context
val conf = new SparkConf().setAppName("Parsing GitHub Commits")
val sc = new SparkContext(conf)

// SparkSQL
val spark = SparkSession.builder().getOrCreate()


val a = new RangePartitioner(50, sc.parallelize((1 to 100).toSeq.map{case x => (x.toString, x)}))
