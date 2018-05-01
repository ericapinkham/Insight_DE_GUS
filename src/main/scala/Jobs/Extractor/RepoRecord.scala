package Jobs.Extractor

import play.api.libs.json.Json

object RepoRecord extends Utils  {
  def apply(rawJson: String): RepoRecord = {
    val parsedJson = Json.parse(removeObjectId(rawJson))

    List(
      parsedJson \ "id",
      parsedJson \ "name",
      parsedJson \ "owner" \ "id",
      parsedJson \ "html_url",
      parsedJson \ "created_at"
    ) match {
      case id :: name :: owner_id :: html_url :: created_at :: Nil =>
        new RepoRecord(
          id.as[Int],
          name.as[String],
          owner_id.as[String],
          html_url.as[String],
          created_at.as[String])
      case _ => throw new Error("Error creating repo record")
    }

  }
}

case class RepoRecord(id: Int, name: String, owner_id: String, html_url: String, created_at: String)