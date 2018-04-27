import GithubCommits
import scala.io.Source

val file = Source.fromFile("/home/eric/Insight/testing_data/github_test_100.json").getLines()

for (line <- file) {
  println(removeObjectId(line))
}

def removeObjectId(input: String): String = {
  input.replaceFirst("""ObjectId\(\s(.*)\s\)""", "$1")
}

