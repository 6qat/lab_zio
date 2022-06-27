package tc

import com.typesafe.config.ConfigFactory
import zio.*

object Progs {

  def nodeConfigLayer: ZLayer[Any, Throwable, Map[String, NodeConfig]] = {

    val prodConfig: Task[NodeConfig] = ZIO.attempt {
      val config = ConfigFactory
        .systemProperties()
        .withFallback(ConfigFactory.load())
      NodeConfig(config.getString("host"), config.getInt("port"))
    }
    val devConfig: UIO[NodeConfig] =
      ZIO.succeed(NodeConfig("localhost", 1020))

    val bothConfig: Task[Map[String, NodeConfig]] = for {
      prod <- prodConfig
      dev <- devConfig
    } yield Map(
      "prod" -> prod,
      "dev" -> dev
    )

    ZLayer.fromZIO(bothConfig)

  }

  object Prog1 extends ZIOAppDefault {

    override lazy val run: ZIO[Any & ZIOAppArgs & Scope, Throwable, Unit] = {
      println("Prod" + "-" * 100)
      nodeConfigLayer {
        lab.MyApp.myApp("prod")
      }
    }

  }

  object Prog2 extends ZIOAppDefault {

    override lazy val run: ZIO[Any & ZIOAppArgs & Scope, Any, Any] = {
      println("Dev" + "-" * 100)
      lab.MyApp.myApp("dev").provide(nodeConfigLayer)
    }
  }

}

import tc.Progs.*
object MainProg extends ZIOApp.Proxy(Prog1 <> Prog2)
