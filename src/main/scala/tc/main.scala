package tc

import zio.*

object Progs {
  object Prog1 extends ZIOAppDefault {

    override lazy val run = {
      // ZIO.none
      lab.program
    }
  }

  object Prog2 extends ZIOAppDefault {
    override lazy val run = {
      lab.program
    }
  }
}

import tc.Progs.*
object MainProg extends ZIOApp.Proxy(Prog1 <> Prog2)
