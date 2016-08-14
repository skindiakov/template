package core.task

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, Props}
import core.{Envelop, Loggable}

import scala.concurrent.duration.Duration

/**
  * Created by stas on 07.08.16.
  *
  * Solves delay problem via delegating scheduled message to child noop-actor
  */

class Delay extends Actor with BaseOperation with Loggable {

  val noopActor = context.actorOf(Props[Noop])

  val defaultDuration = Duration.create(10, TimeUnit.MILLISECONDS)

  override def receive: Receive = {
    case l: LinkTo => noopActor ! l
    case e: Envelop =>
      context.system.scheduler.scheduleOnce(defaultDuration, noopActor, e.copy(wasDelayed = true))

  }
}
