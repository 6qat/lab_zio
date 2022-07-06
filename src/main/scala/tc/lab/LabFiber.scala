package tc.lab
import zio.*
import zio.Console.*

object LabFiber extends ZIOAppDefault {

  val prog1 = for {
    fiber <- ( ZIO.sleep(3.seconds) *> printLine("Hello, after 3 second") *> ZIO.succeed(10) ).fork
    _ <- printLine(s"Hello, World!")
    res <- fiber.join
    _ <- printLine(s"Our fiber succeeded with $res")
  } yield ()

  // =================================================================================================================

  val inner = printLine("Inner job is running.")
    .delay(1.seconds)
    .forever
    .onInterrupt(printLine("Inner job interrupted.").orDie)

  val outer = (
    for {
      f <- inner.forkDaemon
      _ <- printLine("Outer job is running.").delay(1.seconds).forever
      _ <- f.join
    } yield ()
    ).onInterrupt(printLine("Outer job interrupted.").orDie)

  val prog2 = for {
    fiber <- outer.fork
    _     <- fiber.interrupt.delay(3.seconds)
    _     <- ZIO.never
  } yield ()

  // =================================================================================================================


  def run = prog2


}
