package tc.lab

import tc.{AppConfig, NodeConfig}
import zio.*

object MyApp {

  def myApp(
             devOrProd: String = "prod"
           ): ZIO[Map[String, NodeConfig], Nothing, Unit] =
    for {
      config <- ZIO.serviceAt[NodeConfig](devOrProd) // accessing the service
      _ <- ZIO.logInfo(s"Application started with config: ${config.get}")
    } yield ()

}
