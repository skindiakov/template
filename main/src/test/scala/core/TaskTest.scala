package core

import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import core.task._
import org.scalatest._

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Await, ExecutionContext}

/**
  * Created by stas on 07.08.16.
  */
class TaskTest extends BaseTaskTest {


  test("Echo") {
    dispatcher ! AddTask("echo", "echo")
    val result = Await.result(dispatcher ? Execute("message", Option("echo")), duration).asInstanceOf[Envelop]
    assert(result.body == "messagemessage")
  }

  test("Noop") {
    dispatcher ! AddTask("noop", "noop")
    val result = Await.result(dispatcher ? Execute("message", Option("noop")), duration).asInstanceOf[Envelop]
    assert(result.body == "message")
  }

  test("Reverse") {
    dispatcher ! AddTask("reverse", "reverse")
    val result = Await.result(dispatcher ? Execute("message", Option("reverse")), duration).asInstanceOf[Envelop]
    assert(result.body == "message".reverse)
  }

  test("Delay") {
    dispatcher ! AddTask("delay", "delay")
    val result = Await.result(dispatcher ? Execute("message", Option("delay")), duration).asInstanceOf[Envelop]
    assert(result.body == "message")
  }

  test("Echo Reverse") {
    dispatcher ! AddTask("echo", "echo")
    dispatcher ! AddTask("reverse", "reverse")
    dispatcher ! AddLink("echo", "reverse")

    val result = Await.result(dispatcher ? Execute("message", Option("echo")), duration).asInstanceOf[Envelop]
    assert(result.body == "messagemessage".reverse)
  }

  test("Reverse Echo Reverse") {
    dispatcher ! AddTask("echo", "echo")
    dispatcher ! AddTask("reverse", "reverse")
    dispatcher ! AddTask("reverse2", "reverse")
    dispatcher ! AddLink("reverse", "echo")
    dispatcher ! AddLink("echo", "reverse2")

    val result = Await.result(dispatcher ? Execute("message", Option("reverse")), duration).asInstanceOf[Envelop]
    assert(result.body == "messagemessage")
  }

  test("Short Circuit") {
    dispatcher ! AddTask("echo", "echo")
    dispatcher ! AddTask("reverse", "reverse")
    dispatcher ! AddLink("reverse", "echo")
    dispatcher ! AddLink("echo", "reverse")

    Await.result(dispatcher ? Execute("message", Option("reverse")), duration).asInstanceOf[FailedExecution]
  }

}


trait BaseTaskTest extends FunSuite with Matchers with Inside with BeforeAndAfter {

  val duration: FiniteDuration = FiniteDuration(1, TimeUnit.SECONDS)
  implicit val timeout: Timeout = Timeout(duration)
  implicit val defaultContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  var system: ActorSystem = _
  var dispatcher: ActorRef = _

  before {
    system = ActorSystem("my-system")
    dispatcher = system.actorOf(Props[Dispatcher], "dispatcher")

  }

  after {
    system.terminate()
    Await.result(system.whenTerminated, FiniteDuration(5, TimeUnit.SECONDS))
  }
}