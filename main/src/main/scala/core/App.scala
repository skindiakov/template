package core

import akka.actor.{ActorSystem, Props}
import core.helper.{CommandInterpreter, FileReader}
import core.task._

/**
  * Created by stas on 14.08.16.
  */
object App extends Loggable {

  val system = ActorSystem("my-system")
  val dispatcher = system.actorOf(Props[Dispatcher], "dispatcher")
  val interpreter = new CommandInterpreter(dispatcher)

  def main(args: Array[String]) {
    val instructions = if (args.isEmpty) {
      "instructions.txt"
    } else
      args.head

    try {
      FileReader.readInstructions(instructions).foreach { command =>
        interpreter.sendToExecution(command._1, command._2)
      }
    } finally {
      system.terminate()
    }
  }
}
