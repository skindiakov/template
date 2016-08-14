package core.task

import akka.actor.{Actor, ActorRef}
import core.{Envelop, FailedExecution, ShortCircuit}

/**
  * Created by stas on 14.08.16.
  */
trait BaseOperation {
  own: Actor =>

  implicit val defaultContext = scala.concurrent.ExecutionContext.Implicits.global

  var linkedTo: Option[ActorRef] = None

  def receive: Receive = {
    case e: Envelop if e.trace.contains(self.path.name) =>
      e.initialSender ! FailedExecution(addTrace(e), ShortCircuit)
    case LinkTo(ref) => linkedTo = Option(ref)
    case e: Envelop =>
      destination(e) ! addTrace(operation(e))

  }

  def destination(e: Envelop): ActorRef = {
    linkedTo.getOrElse(e.initialSender)
  }

  def operation(e: Envelop) = e

  def addTrace(e: Envelop): Envelop = {
    e.copy(trace = e.trace :+ self.path.name)
  }
}
