package core.helper

import java.util.concurrent.TimeUnit

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import core.Envelop
import core.task.{AddLink, AddTask, Execute}

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * Created by stas on 14.08.16.
  */
class CommandInterpreter(dispatcher: ActorRef) {
  // Global context is not the best way, but appropriate for test task
  implicit val defaultContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  val duration: FiniteDuration = FiniteDuration(10, TimeUnit.SECONDS)
  implicit val timeout: Timeout = Timeout(duration)

  val DelayedKey = "tbb"

  def sendToExecution(head: String, params: List[String]): Unit = {
    head.toLowerCase() match {
      case "task" => dispatcher ! AddTask(params.head, params(1))
      case "link" => dispatcher ! AddLink(params.head, params(1))
      case "process" =>
        val f = Future.sequence(params.map(p => dispatcher ? Execute(p)))
        val envelops: List[Envelop] = Await.result(f, duration).asInstanceOf[List[Envelop]]
        val lines: List[String] = if (envelops.exists(e => e.wasDelayed)) {
          DelayedKey :: envelops.map(_.body)
        } else envelops.map(_.body)

        printResponse(lines)
    }
  }
/*
  From Task Text:
  A process statement should leave the network “dry”: no events are left in the network once processing results are printed.

  So println is quite appropriate solution here.
  Another possibility is to set up logger to log only incomingmessage.
 */
  def printResponse(response: List[String]) = println(response mkString " ")
}
