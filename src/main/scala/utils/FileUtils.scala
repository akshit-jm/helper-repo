package utils

import java.net.URL
import scala.io.Source

object FileUtils {
  def readDataFromFile(fileName: URL): List[String] = {
    val source = Source.fromURL(fileName)
    try {
      source.getLines().toList
    } finally {
      source.close()
    }
  }
}
