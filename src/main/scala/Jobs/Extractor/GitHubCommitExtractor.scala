package Jobs.Extractor

import play.api.libs.json._

import scala.util.matching.Regex


object GitHubCommitExtractor {
  // Set aside the fields we care about

  val languageNames: Map[String, String] =
    Map(
      "py" -> "python",
      "hs" -> "haskell",
      "scala" -> "scala",
      "java" -> "java",
      "js" -> "javascript"
    )
  val languagePatterns: Map[String, List[String]] =
    Map(
      "python" -> List("""import\s+([0-9a-zA-Z]+)""", """from\s+(\w+?)\s+import\s+(?:[0-9a-zA-Z])"""),
      "scala" -> List("""import\s+([0-9a-zA-Z\.]*[0-9a-zA-Z]+)"""),
      "haskell" -> List("""import\s+(?!qualified)\s*([0-9a-zA-Z]+)""", """import\squalified\s([0-9a-zA-Z]+)"""), // Could probably do this with optional non-capture group
      "java" -> List("""import\s+(?!static)\s*([0-9a-zA-Z\.]*[0-9a-zA-Z]+)""", """import\s+static\s+([0-9a-zA-Z\.]*[0-9a-zA-Z]+)""")
    )

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

  def extract(rawJson: String): List[Commit] = {
    val lol = for (file <- parseMetaData(rawJson) )
      yield file match {
        case date :: email :: message :: filename :: status :: patch :: Nil =>
          extractPackages(extractLanguage(filename), patch).map{
            case (count, packageName) => new Commit(date, email, message, filename, extractLanguage(filename), packageName, count)
          }
        case _ => throw new Error("Incorrect output from something")
    }
    
    lol.flatten
  }
  
  private def parseMetaData(rawJson: String): List[List[String]] = {
    /**
      *
      * @param input
      * @return
      */
    def removeObjectId(input: String): String = input.replaceFirst("""ObjectId\(\s(\"[0-9a-z]*\")\s\)""", "$1")
    
    def filesInfo(filesObject: JsValue): List[List[String]] = {
      val fileFields = List("filename", "status", "patch") //commit/files/#/
      val files = (filesObject \ "files").get.as[List[Map[String, JsValue]]]
      
      def getIfDefined(file: Map[String, JsValue])(key: String): String =
        if (file.isDefinedAt(key)) file(key).as[String] else ""
      
      files.map(x => fileFields.map(getIfDefined(x)(_)))
    }
    
    def commitInfo(commit: JsValue): List[String] = {
      val notFound = (Json.parse("""{"NotFound": ""}""") \ "NotFound").get // Way to avoid squacking if the value isn't where it's supposed to be. There has to be a better way.
      List(
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
