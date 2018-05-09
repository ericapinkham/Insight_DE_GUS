package Jobs.Extractor

import scala.util.matching.Regex
import play.api.libs.json.{JsValue, Json}
import scala.math.Ordered.orderingToOrdered

object CommitRecord extends Languages {

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

  def extractCommit(rawJson: String, date: String): List[CommitRecord] = {
    try {
      extract(rawJson, date)
    } catch { // If we fail to parse a record, we don't want everything to stop.
      case _: Throwable => List[CommitRecord]()
    }
  }

  def extractDate(dateTime: String, default: String): String = {
    val datePattern = """(\d{4}-\d{2}-\d{2})T\d{2}:\d{2}:\d{2}Z""".r
    
    dateTime match {
      case datePattern(dateString) => dateString
      case _ => default
    }
  }

  private def extract(rawJson: String, defaultDate: String): List[CommitRecord] = {
    // Remove the MongoDB ObjectID
    def removeObjectId(input: String): String = input.replaceFirst("""ObjectId\(\s(\"[0-9a-z]*\")\s\)""", "$1")
    val jsonCommit = Json.parse(removeObjectId(rawJson))

    val commitDate = extractDate((jsonCommit \ "commit" \ "committer" \ "date").validate[String].getOrElse(null), defaultDate)

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
            new CommitRecord(defaultDate, commitDate, language, packageName, count)
      }
    }
  }
}

case class CommitRecord(received_date: String, commit_date: String, language_name: String, import_name: String, usage_count: Int)  extends Ordered[CommitRecord] {
  // Define an ordering so that Spark can repartition appropriately
  def compare(that: CommitRecord): Int = (this.commit_date, this.language_name, this.import_name) compare (that.commit_date, that.language_name, import_name)
}
