package tc

import zio.*

object Main extends ZIOAppDefault {

  override lazy val run = {
    //ZIO.none
    lab.program.exitCode
  }

}
