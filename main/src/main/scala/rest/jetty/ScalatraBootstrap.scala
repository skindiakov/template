package rest.jetty

import javax.servlet.ServletContext

import akka.actor.ActorSystem
import org.scalatra.LifeCycle
import org.scalatra.swagger.{Swagger, ApiInfo}
import rest.controller.GreetingController

class ScalatraBootstrap extends LifeCycle {
  lazy val system = ActorSystem("ProcessingSystem")
  lazy implicit val swagger = new  Swagger(Swagger.SpecVersion, "1.0.0", SystemApiInfo)


  override def init(context: ServletContext): Unit = {
    context.mount(new GreetingController, "/sample/*", "greetings")
  }

  override def destroy(context:ServletContext): Unit = {
    system.terminate()
  }
}

object ScalatraBootstrap {

}

object SystemApiInfo extends ApiInfo(
  title = "REST API",
  description = "REST API",
  termsOfServiceUrl = "",
  contact = "no-contact@gmail.com",
  license = "Proprietary",
  licenseUrl = "")

