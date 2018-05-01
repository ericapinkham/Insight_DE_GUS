package Jobs.Extractor

trait Utils {
  final def removeObjectId(input: String): String = input.replaceFirst("""ObjectId\(\s(\"[0-9a-z]*\")\s\)""", "$1")
//
//  final val notFound = (Json.parse("""{"NotFound": ""}""") \ "NotFound").get // Way to avoid squacking if the value isn't where it's supposed to be. There has to be a better way.
}
