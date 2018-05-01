package Jobs.Extractor

import play.api.libs.json.{JsValue, Json}

import scala.util.matching.Regex

object CommitRecord extends Utils with Languages {
  def apply(tuple: (String, String, String, String, String, String, String, Int)): CommitRecord = tuple match {
    case (id, date, email, message, filename, language, packageName, count) => new CommitRecord(id.toInt, date, email, message, filename, language, packageName, count)
  }

  def apply(rawJson: String): List[CommitRecord] = {
    val lol = for (file <- parseMetaData(rawJson) )
      yield file match {
        case id :: date :: email :: message :: filename :: status :: patch :: Nil =>
          extractPackages(extractLanguage(filename), patch).map{
            case (count, packageName) => new CommitRecord(id.toInt, date, email, message, filename, extractLanguage(filename), packageName, count)
          }
        case _ => throw new Error("Invalid parameter")
      }
    lol.flatten
  }

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

  private def parseMetaData(rawJson: String): List[List[String]] = {

    def filesInfo(filesObject: JsValue): List[List[String]] = {
      val fileFields = List("filename", "status", "patch") //commit/files/#/
      val files = (filesObject \ "files").get.as[List[Map[String, JsValue]]]

      def getIfDefined(file: Map[String, JsValue])(key: String): String =
        if (file.isDefinedAt(key)) file(key).as[String] else ""

      files.map(x => fileFields.map(getIfDefined(x)(_)))
    }

    def commitInfo(commit: JsValue): List[String] = {
      List(
        commit \ "commit" \ "committer" \ "id",
        commit \ "commit" \ "committer" \ "date",
        commit \ "commit" \ "committer" \ "email",
        commit \ "commit" \ "message"
      ).map(_.getOrElse(notFound).as[String])
    }

    // Actually do the work
    val commit = Json.parse(removeObjectId(rawJson))

    filesInfo(commit).map(commitInfo(commit) ::: _)
  }

}

/*
  commit_timestamp DATETIME NOT NULL,
  user_email VARCHAR(255),
  commit_message TEXT,
  file_name VARCHAR(32) NOT NULL,
  -- patch TEXT,
  language_name VARCHAR(32) NOT NULL,
  package_name VARCHAR(32) NOT NULL,
  usage_count INT NOT NULL DEFAULT 1
  */
case class CommitRecord(id: Int, commit_timestamp: String, user_email: String, commit_message: String, file_name: String, language_name: String, package_name: String, usage_count: Int) {

}
