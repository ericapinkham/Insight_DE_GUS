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
      jsonMap.getOrElse("location", null).asInstanceOf[String],
      jsonMap.getOrElse("email", null).asInstanceOf[String],
      jsonMap.getOrElse("login", null).asInstanceOf[String]
    )
  }
}

case class UserRecord(id: Int, created_at: String, location: String, email: String, login: String) {
  def +(user2: UserRecord): UserRecord = {
    def max[T](x : T, y : T)(implicit f: T => Ordered[T]) = if (x > y) x else y
    (this, user2) match {
      case (UserRecord(id1, created_at1, location1, email1, login1), UserRecord(id2, created_at2, location2, email2, login2)) if id1 == id2 =>
        new UserRecord(id1, max(created_at1, created_at2), max(location1, location2), max(email1, email2), max(login1,login2))
      case (_, _) => throw new Error("Keys do not match")
    }
  }
}
