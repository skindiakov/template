package core.task

import akka.actor.Actor
import core.{Envelop, Loggable}

/**
  * Created by stas on 07.08.16.
  */
class Echo extends Actor with BaseOperation with Loggable {

  override def operation(envelop: Envelop): Envelop = {
    val body = envelop.body
    envelop.copy(body = body + body)
  }

}
