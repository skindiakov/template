package core

import akka.actor.ActorRef

/**
  * Created by stas on 07.08.16.
  */
case class Envelop(body: String, initialSender: ActorRef, trace: Seq[String] = Seq(), wasDelayed: Boolean = false)

case class FailedExecution(envelop: Envelop, error: Exception)

object ShortCircuit extends RuntimeException
