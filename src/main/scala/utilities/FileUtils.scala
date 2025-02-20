package utilities

import io.circe.Encoder
import io.circe.syntax.EncoderOps

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import scala.io.Source

object FileUtils {
  def readDataFromFile(fileName: String): List[String] = {
    val source = Source.fromFile(fileName)
    try {
      source.getLines().toList
    } finally {
      source.close()
    }
  }

  def saveToFile[A: Encoder](filename: String, data: List[A]): Unit = {
    // Convert the list to a JSON string
    val jsonString = data.asJson.noSpaces // You can use .spaces2 for pretty-printing

    // Write the JSON string to the file
    Files.write(Paths.get(filename), jsonString.getBytes(StandardCharsets.UTF_8))
  }
}
