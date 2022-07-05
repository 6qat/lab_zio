package tc

import tc.Progs.*
import zio.*

object Main extends ZIOApp.Proxy(Prog1 <> Prog2)
