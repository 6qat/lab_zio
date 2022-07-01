package tc.lab

import zhttp.http.*
import zhttp.service.Server
import zio.ZIOAppDefault
import zio.*

object MyZioHttpServer extends ZIOAppDefault  {

  val app1 = Http.text("Just one request...")
  val app2 = Http.collect[Request] {
    case Method.GET -> !! / "fruits" / "a"  => Response.text("Apple")
    case Method.GET -> !! / "fruits" / "b"  => Response.text("Banana")
  }

//  zio.Runtime.defaultExecutor

   def run = Server.start(8990, app2)

}
