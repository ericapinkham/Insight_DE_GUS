

import Jobs.Extractor.CommitRecord

import scala.io.Source.fromFile
import Jobs.Extractor.CommitRecord.{extractLanguage, extractPackages, removeObjectId}
import scala.util.parsing.json.JSON


def extract(rawJson: String): List[CommitRecord] = {
  try {
    extractCommit(rawJson)
  } catch {
    case _ => List[CommitRecord]()
  }
}

def extractCommit(rawJson: String): List[CommitRecord] = {
  case class FileInfo(fileName: String, language: String, status: String, packageName: String, packageUsage: Int)

  val jsonMap = JSON.parseFull(removeObjectId(rawJson))
    .getOrElse(Map[String, Any]())
    .asInstanceOf[Map[String, Any]]

  val committerMap = jsonMap.getOrElse("committer", Map[String, Any]()).asInstanceOf[Map[String, Any]]
  val id: Int = committerMap.getOrElse("id", null).asInstanceOf[Double].toInt
  val date: String = committerMap.getOrElse("date", null).asInstanceOf[String]
  val email: String = committerMap.getOrElse("email", null).asInstanceOf[String]
  val message: String = committerMap.getOrElse("message", null).asInstanceOf[String]

  val fileMaps = jsonMap.getOrElse("files", List[Map[String, Any]]()).asInstanceOf[List[Map[String, Any]]]

  val fileTuples = fileMaps.map{f => (
    f.getOrElse("filename", null).asInstanceOf[String],
    extractLanguage(f.getOrElse("filename", null).asInstanceOf[String]),
    f.getOrElse("status", null).asInstanceOf[String],
    f.getOrElse("patch", null).asInstanceOf[String]
  )
  }

  fileTuples.flatMap{
    case (filename, language, status, patch) =>
      extractPackages(language, patch).map{
        case (count, packageName) =>
          new CommitRecord(id, date, email, message, filename, language, packageName, count)
      }
  }
}

val lines = fromFile("/home/eric/Insight/testing_data/github_test_100.json").getLines()


for {line <- lines.flatMap(extract(_))} println(line)




