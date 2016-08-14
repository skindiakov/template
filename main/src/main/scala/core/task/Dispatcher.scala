package core.task

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef, Props}
import akka.util.Timeout
import core.{Envelop, Loggable}

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration

/**
  * Created by stas on 07.08.16.
  */
class Dispatcher extends Actor with Loggable {
  override def receive: Receive = {
    case AddTask(name, task) =>
      if (defined(task)) {
        context.actorOf(task, name = name)
      }
    case AddLink(from, to) =>
      (get(from), get(to)) match {
        case (Some(f), Some(t)) =>
          f ! LinkTo(t)
        case _ =>
      }
    case Execute(message, task) =>
      select(task) ! Envelop(message, sender())
    case Ping => sender ! Pong
  }

  implicit val timeout = Timeout(FiniteDuration(1, TimeUnit.SECONDS))

  def get(name: String): Option[ActorRef] = {
    Await.ready(select(name).resolveOne(), FiniteDuration(1, TimeUnit.SECONDS)).value.get.toOption
  }

  def select(name: String) = context.actorSelection(s"$name")

  def defined(task: String) = {
    taskRepo.isDefinedAt(task)
  }

  implicit val taskRepo: PartialFunction[String, Props] = PartialFunction {
    case "echo" => Props[Echo]
    case "noop" => Props[Noop]
    case "reverse" => Props[Reverse]
    case "delay" => Props[Delay]
  }
}

case class AddTask(name: String, task: String)

case class AddLink(from: String, to: String)

case class Execute(message: String, task: String)

case class LinkTo(ref: ActorRef)

object Ping

object Pong
