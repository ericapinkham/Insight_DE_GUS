package Jobs.Extractor

import scala.util.parsing.json.JSON

object UserRecord extends Utils {
  def apply(rawJson: String): UserRecord = {
    val jsonMap = JSON.parseFull(removeObjectId(rawJson))
      .getOrElse(Map[String, Any]())
      .asInstanceOf[Map[String, Any]]

    new UserRecord(
      jsonMap.getOrElse("id", null).asInstanceOf[Double].toInt,
      jsonMap.getOrElse("created_at", null).asInstanceOf[String],
      jsonMap.getOrElse("location", null).asInstanceOf[String]
    )
  }
}

case class UserRecord(id: Int, created_at: String, location: String)
