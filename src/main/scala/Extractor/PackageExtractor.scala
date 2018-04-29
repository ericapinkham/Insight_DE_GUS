package Extractor

import scala.util.matching.Regex

trait PackageExtractor {
  
  val languageNames: Map[String, String] = Map(
    "py" -> "python",
    "hs" -> "haskell",
    "scala" -> "scala",
    "java" -> "java",
    "js" -> "javascript"
  )
  val languagePatterns: Map[String, List[Regex]] = Map("python" -> List("""(\+|\-)import\s(\w+?)(?:\s|$)""".r, """(\+|\-)from\s(\w+?)\simport\s(?:[A-Za-z])""".r))
  
  def extractLanguage(filename: String): String = {
    """\.([a-zA-Z0-9]+)""".r.findFirstMatchIn(filename.trim) match {
      case None => ""
      case Some(m) => languageNames.getOrElse(m.group(1), "")
    }
  }
  
  def extractPackages(language: String, patch: String): List[(Int, String)] =
    languagePatterns.getOrElse(language, List[Regex]())
      .flatMap(_.findAllMatchIn(patch).map(m => (if (m.group(1) == "+") 1 else -1, m.group(2))))
  
}
