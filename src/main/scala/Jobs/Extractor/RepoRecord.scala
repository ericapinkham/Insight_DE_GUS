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
    ).map(_.getOrElse(notFound).as[String]) match {
      case id :: name :: owner_id :: html_url :: created_at :: Nil => new RepoRecord(id.toInt, name, owner_id, html_url, created_at)
      case _ => throw new Error("Error creating repo record")
    }

  }
}

case class RepoRecord(id: Int, name: String, owner_id: String, html_url: String, created_at: String)