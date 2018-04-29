name := "insight_data_engineering"

version := "0.1"

scalaVersion := "2.11.8" // "2.12.5"

val sparkVersion = "2.2.1"

resolvers ++= Seq(
  "apache-snapshots" at "http://repository.apache.org/snapshots/"
)

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion
)


libraryDependencies += "com.typesafe.play" %% "play-json" % "2.6.7"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

// libraryDependencies += "mysql" % "mysql-connector-java" % "6.0.5"

// Dealing with conflicting file paths
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

//assemblyMergeStrategy in assembly := {
//  case PathList("META-INF", xs@_*) =>
//    xs.map(_.toLowerCase) match {
//      case ("manifest.mf" :: Nil) |
//           ("index.list" :: Nil) |
//           ("dependencies" :: Nil) |
//           ("license" :: Nil) |
//           ("notice" :: Nil) => MergeStrategy.discard
//      case _ => MergeStrategy.first // was 'discard' previousely
//    }
//  case "reference.conf" => MergeStrategy.concat
//  case _ => MergeStrategy.first
//}