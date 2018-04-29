package Extractor

import java.sql.Timestamp

object Commit {
  def apply(tuple: (String, String, String, String, String, String, Int)): Commit = tuple match {
    case (date, email, message, filename, language, packageName, count) => new Commit(date, email, message, filename, language, packageName, count)
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
case class Commit(commit_timestamp: String, user_email: String, commit_message: String, file_name: String, language_name: String, package_name: String, usage_count: Int) {

}
