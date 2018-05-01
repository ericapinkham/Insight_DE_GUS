package Jobs.Extractor

import play.api.libs.json.Json

object UserRecord extends Utils {
  def apply(rawJson: String): UserRecord = {
    val parsedJson = Json.parse(removeObjectId(rawJson))

    List(
      parsedJson \ "id",
      parsedJson \ "created_at",
      parsedJson \ "location"
    ).map(_.getOrElse(notFound).as[String]) match {
      case id :: created_at :: location :: Nil => new UserRecord(id.toInt, created_at, location)
      case _ => throw new Error("Error creating user record")
    }

  }
}

case class UserRecord(id: Int, created_at: String, location: String)
