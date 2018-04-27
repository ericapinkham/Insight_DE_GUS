package Extractor

import spray.json._

object GithubCommitExtractor {

  // Define the JSON protocol, because JSON is a wonderful, wonderful serialization.
  case class File(additions: Int,
                  changes: Int,
                  contents_url: String,
                  raw_url: String ,
                  filename: String,
                  status: String,
                  sha: String ,
                  deletions: Int,
                  patch: Option[String],
                  blob_url: String
                 )
  case class FileList(files: List[File])

  object CommitFileProtocol extends DefaultJsonProtocol {
    implicit val FileFormat = jsonFormat10(File)
  }

  import CommitFileProtocol._

  // Nice extractor method
  def extractCommitMetaData(rawJson: String): List[(String, String)] = {
    def removeObjectId(input: String): String = input.replaceFirst("""ObjectId\(\s(\"[0-9a-z]*\")\s\)""", "$1")

    def extractFile(file: File): (String, String) =
      file match {
        case File(additions,
        changes,
        contents_url,
        raw_url,
        filename,
        status,
        sha,
        deletions,
        patch,
        blob_url
        ) => (filename, patch.getOrElse(""))
        case _ => ("", "")
      }

    try {
      removeObjectId(rawJson).parseJson.asJsObject.getFields("files").head.convertTo[List[File]].map(extractFile)
    } catch  {
      case jsonSucks: spray.json.DeserializationException => List(("", ""))
      case _ => List(("",""))
    }


  }
}
