package Extractor

import play.api.libs.json._

object GithubCommitExtractor extends PackageExtractor {
  // Set aside the fields we care about
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
  def extract(rawJson: String): List[(String, String, String, String, String, String, Int)] = {
    val lol = for (file <- parseMetaData(rawJson) )
      yield file match {
        case date :: email :: message :: filename :: status :: patch :: Nil =>
          extractPackages(extractLanguage(filename), patch).map{
            case (count, packageName) => (date, email, message, filename, extractLanguage(filename), packageName, count)
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
