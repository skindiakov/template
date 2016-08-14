package core.helper

import java.io.File

import core.Loggable

import scala.io.Source

/**
  * Created by stas on 14.08.16.
  */
object FileReader extends Loggable {

  def readInstructions(instructions: String): Stream[(String, List[String])] = {
    val file = new File(instructions)
    if (!file.exists()) {
      log error s"No file found [${file.getAbsolutePath}]"
      throw new RuntimeException("file not found")
    }
    Source.fromFile(file).getLines().toStream.filter(!_.trim.isEmpty).map { line =>
      line.split(" ").toList match {
        case head :: params => (head, params)
        case e => throw new RuntimeException(s"Bad instruction [$e]")
      }
    }
  }
}
