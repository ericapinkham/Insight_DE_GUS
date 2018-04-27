name := "insight_data_engineering"

version := "0.1"

scalaVersion := "2.11.8" // "2.12.5"

val sparkVersion = "2.3.0"

resolvers ++= Seq(
  "apache-snapshots" at "http://repository.apache.org/snapshots/"
)

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion
)

// https://mvnrepository.com/artifact/org.apache.hadoop/hadoop-aws
//libraryDependencies += "org.apache.hadoop" % "hadoop-aws" % "2.6.0"