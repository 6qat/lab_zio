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
    )(r => Console.printLine("About to end ActorSystem...").orDie *> ZIO.succeed(r.terminate()))

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

  val app: ZIO[Any, Throwable, Unit] = ZIO.scoped {
    scopedActorSystem.flatMap { implicit system =>
      for {
        sb <- ZIO.fromFuture(ec =>
          Http().newServerAt("localhost", 135).bind(route)
        )
        _ <- Console.printLine(s"Server bound to ${sb.localAddress}")
        _ <- Console.readLine("Press any key...")
        _ <- ZIO.fromFuture(ec => sb.unbind())
      } yield ()

    }
  }

  override def run: ZIO[Any, Throwable, Unit] = app

}
