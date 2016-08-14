package core.task

import akka.actor.Actor
import core.{Envelop, Loggable}

/**
  * Created by stas on 07.08.16.
  */
class Reverse extends Actor with BaseOperation with Loggable {

  override def operation(envelop: Envelop): Envelop = {
    envelop.copy(body = envelop.body.reverse)
  }
}
