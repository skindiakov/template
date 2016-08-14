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

  var defaultTask:Option[String] = None

  override def receive: Receive = {
    case AddTask(name, taskType) =>
      if (defined(taskType)) {
        val task = context.actorOf(taskType, name = name)
        if(defaultTask.isEmpty) defaultTask = Option(task.path.name)
      }
    case AddLink(from, to) =>
      (get(from), get(to)) match {
        case (Some(f), Some(t)) =>
          f ! LinkTo(t)
        case _ =>
      }
    case Execute(message, task) =>
      select(task.getOrElse(defaultTask.get)) ! Envelop(message, sender())
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

trait Operation

case class AddTask(name: String, task: String) extends Operation

case class AddLink(from: String, to: String) extends Operation

case class Execute(message: String, task: Option[String]= None) extends Operation

case class LinkTo(ref: ActorRef) extends Operation

object Ping

object Pong
