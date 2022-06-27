package tc.lab

import com.typesafe.config.ConfigFactory
import tc.{AppConfig, NodeConfig}
import zio.*

val nodeConfigLayer: ZLayer[Any, Throwable, NodeConfig] = {

  lazy val task: Task[NodeConfig] = ZIO.attempt {
    val config = ConfigFactory
      .systemProperties()
      .withFallback(ConfigFactory.load())
    NodeConfig(config.getString("host"), config.getInt("port"))
  }

  ZLayer.fromZIO(task)

}

val myApp: ZIO[NodeConfig, Nothing, Unit] =
  for {
    config <- ZIO.service[NodeConfig] // accessing the service
    _ <- ZIO.logInfo(s"Application started with config: $config")
  } yield ()

val program: ZIO[Any, Throwable, Unit] = {
  println("-" * 100)

//  nodeConfigLayer {
  //    myApp
  //  } // same as bellow

  myApp.provideLayer(nodeConfigLayer) // same as above
}
