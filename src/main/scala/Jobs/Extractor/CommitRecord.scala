package Jobs.Extractor

import scala.util.matching.Regex
import scala.util.parsing.json.JSON

object CommitRecord extends Utils with Languages {
  def extractLanguage(filename: String): String = {
    """\.([a-zA-Z0-9]+)""".r.findFirstMatchIn(filename.trim) match {
      case None => ""
      case Some(m) => languageNames.getOrElse(m.group(1), "")
    }
  }

  def extractPackages(language: String, patch: String): List[(Int, String)] = {
    def prepPattern(pattern: String): Regex = ("""(\+|\-)\s*""" + pattern).r
    languagePatterns.getOrElse(language, List[String]())
      .flatMap(prepPattern(_).findAllMatchIn(patch).map(m => (if (m.group(1) == "+") 1 else if (m.group(1) == "-") -1 else 0, m.group(2))))
  }

  def extractCommit(rawJson: String): List[CommitRecord] = {
    try {
      extract(rawJson)
    } catch {
      case _ => List[CommitRecord]()
    }
  }

  private def extract(rawJson: String): List[CommitRecord] = {
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
}

case class CommitRecord(id: Int, commit_timestamp: String, user_email: String, commit_message: String, file_name: String, language_name: String, package_name: String, usage_count: Int)
