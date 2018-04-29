package Extractor

import scala.util.matching.Regex

trait PackageExtractor {
  
  val languageNames: Map[String, String] =
    Map(
      "py" -> "python",
      "hs" -> "haskell",
      "scala" -> "scala",
      "java" -> "java",
      "js" -> "javascript"
    )
  val languagePatterns: Map[String, List[String]] =
    Map(
      "python" -> List("""import\s+([0-9a-zA-Z]+)""", """from\s+(\w+?)\s+import\s+(?:[0-9a-zA-Z])"""),
      "scala" -> List("""import\s+([0-9a-zA-Z\.]*[0-9a-zA-Z]+)"""),
      "haskell" -> List("""import\s+(?!qualified)\s*([0-9a-zA-Z]+)""", """import\squalified\s([0-9a-zA-Z]+)"""), // Could probably do this with optional non-capture group
      "java" -> List("""import\s+(?!static)\s*([0-9a-zA-Z\.]*[0-9a-zA-Z]+)""", """import\s+static\s+([0-9a-zA-Z\.]*[0-9a-zA-Z]+)""")
    )
  
  def extractLanguage(filename: String): String = {
    """\.([a-zA-Z0-9]+)""".r.findFirstMatchIn(filename.trim) match {
      case None => ""
      case Some(m) => languageNames.getOrElse(m.group(1), "")
    }
  }
  
  def extractPackages(language: String, patch: String): List[(Int, String)] = {
    def prepPattern(pattern: String): Regex = ("""(\+|\-)\s*""" + pattern).r
    languagePatterns.getOrElse(language, List[String]())
      .flatMap(prepPattern(_).findAllMatchIn(patch).map(m => (if (m.group(1) == "+") 1 else if (m.group(1) == "-") -1 else 0, m.group(2))))
  }
}
