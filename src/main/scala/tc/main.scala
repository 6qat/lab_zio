package tc

import com.typesafe.config.ConfigFactory
import zio.*

import scala.collection.immutable.Map

object Progs {

  def nodeConfigLayer: ZLayer[Any, Throwable, Map[String, NodeConfig]] = {

    val prodConfig: Task[NodeConfig] = ZIO.attempt {
      val config = ConfigFactory
        .systemProperties()
        .withFallback(ConfigFactory.load())
      NodeConfig(config.getString("host"), config.getInt("port"))
    }
    val bothConfig: Task[Map[String, NodeConfig]] = for {
      prod <- prodConfig
      dev = NodeConfig("localhost", 2020)
    } yield Map(
      "prod" -> prod,
      "dev" -> dev
    )


    ZLayer.fromZIO(bothConfig)

  }

  object Prog1 extends ZIOAppDefault {

    override lazy val run = {
      // ZIO.none

      println("Prod" + "-" * 100)

      //  nodeConfigLayer {
      //    myApp
      //  } // same as bellow

      lab.myApp().provideLayer(nodeConfigLayer) // same as above
    }


  }

  object Prog2 extends ZIOAppDefault {
    override lazy val run = {
      println("Dev" + "-" * 100)
      lab.myApp("dev").provideLayer(nodeConfigLayer)
    }
  }
}

import tc.Progs.*

object MainProg extends ZIOApp.Proxy(Prog1 <> Prog2)
