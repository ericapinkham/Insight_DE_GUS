package GithubCommits

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
                  patch: String,
                  blob_url: String
                 )
  case class FileList(files: List[File])

  object CommitFileProtocol extends DefaultJsonProtocol {
    implicit val FileFormat = jsonFormat10(File))
  }

  import CommitFileProtocol._

  // Nice extractor method
  def extractCommitMetaData(rawJson: String): List[(String, String)] = {
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
        ) => (filename, patch)
        case _ => ("", "")
      }

    rawJson.parseJson.asJsObject.getFields("files").head.convertTo[List[File]].map(extractFile)
  }
}
