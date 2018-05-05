package Jobs.Extractor

import scala.util.matching.Regex
import play.api.libs.json.{Json, JsValue}

object CommitRecord extends Utils with Languages {
  // If dates don't parse, use today's date
  private lazy val today = new java.sql.Date(new java.util.Date().getTime)

  def extractLanguage(filename: String): String = {
    """\.([a-zA-Z0-9]+)""".r.findFirstMatchIn(filename.trim) match {
      case None => ""
      case Some(m) => languageNames.getOrElse(m.group(1), "")
    }
  }

  def extractPackages(language: String, patch: String): List[(Int, String)] = {
    def prepPattern(pattern: String): Regex = ("""(\+|\-)\s*""" + pattern).r
    languagePatterns.getOrElse(language, List[String]())
      .flatMap(prepPattern(_)
      .findAllMatchIn(patch)
      .map(m => (if (m.group(1) == "+") 1 else if (m.group(1) == "-") -1 else 0, m.group(2))))
  }

  def extractCommit(rawJson: String): List[CommitRecord] = {
    try {
      extract(rawJson)
    } catch { // If we fail to parse a record, we don't want everything to stop.
      case _: Throwable => List[CommitRecord]()
    }
  }

  def extractDate(dateTime: String): java.sql.Date = {
    val datePattern = """(\d{4})-(\d{2})-(\d{2}).*""".r
    dateTime match {
      case datePattern(year, month, day) => new java.sql.Date(year.toInt, month.toInt, day.toInt)
      case _ => today
    }
  }

  private def extract(rawJson: String): List[CommitRecord] = {
    val jsonCommit = Json.parse(removeObjectId(rawJson))

    val date = extractDate((jsonCommit \ "commit" \ "committer" \ "date").validate[String].getOrElse(null))

    val files = (jsonCommit \ "files").validate[List[JsValue]].getOrElse(List[JsValue]()) //.getOrElse(List[Map[String, Any]]())
    val fileTuples = for (file <- files) yield (
      extractLanguage((file \ "filename").validate[String].getOrElse("")),
      (file \ "patch").validate[String].getOrElse("")
    )

    // Extract all of the imports and flatten
    fileTuples.flatMap{
      case (language, patch) =>
        extractPackages(language, patch).map{
          case (count, packageName) =>
            new CommitRecord(date, language, packageName, count)
      }
    }
  }
}

case class CommitRecord(commit_date: java.sql.Date, language_name: String, package_name: String, usage_count: Int)
