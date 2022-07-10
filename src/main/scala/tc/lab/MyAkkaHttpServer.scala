package tc.lab

import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{complete, get, path}
import akka.http.scaladsl.server.Route
import zio.{Console, Scope, ZIO, ZIOAppDefault}

object MyAkkaHttpServer extends ZIOAppDefault {

  val scopedActorSystem: ZIO[Any & Scope, Throwable, ActorSystem[Nothing]] =
    ZIO.acquireRelease(
      ZIO.attempt(ActorSystem(Behaviors.empty, "system"))
    )(r => ZIO.succeed(r.terminate()))

  val route: Route =
    path("hello") {
      get {
        complete(
          HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            "<h1>Say hello to akka-http</h1>"
          )
        )
      }
    }

  val app = ZIO.scoped {
    scopedActorSystem.flatMap { implicit system =>
      for {
        sb <- ZIO.fromFuture(_ =>
          Http().newServerAt("localhost", 8080).bind(route)
        )
        _ <- Console.printLine(s"Server bound to ${sb.localAddress}")
        _ <- Console.readLine("Press any key...")
        _ <- ZIO.fromFuture(_ => sb.unbind())
      } yield ()

    }
  }

  override def run = app

}
