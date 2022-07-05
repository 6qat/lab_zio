package tc

import tc.Progs.*
import zio.*

object MainProg extends ZIOApp.Proxy(Prog1 <> Prog2)
