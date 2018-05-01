package Jobs.Extractor

trait Languages {
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
}
