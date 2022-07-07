package tc.lab
import zio.*
import zio.Console.*

object LabFiber extends ZIOAppDefault {

  val prog1 = for {
    fiber <- (ZIO.sleep(3.seconds) *> printLine("Hello, after 3 second") *> ZIO
      .succeed(10)).fork
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
    _ <- fiber.interrupt.delay(3.seconds)
    _ <- ZIO.never
  } yield ()

  // =================================================================================================================

  import zio.Console.*

  val mayFail = for {
    b <- Random.nextBoolean
    fiber <- (if (b) ZIO.succeed(10)
              else ZIO.fail("The boolean was not true")).fork
    exitValue <- fiber.await
    _ <- exitValue match {
      case Exit.Success(value) => printLine(s"Fiber succeeded with $value")
      case Exit.Failure(cause) => printLine(s"Fiber failed: $cause")
    }
  } yield ()

  // =================================================================================================================

  def fib(n: Int): UIO[Int] =
    if (n <= 1) {
      ZIO.succeed(1)
    } else {
      for {
        fiber1 <- fib(n - 2).fork
        fiber2 <- fib(n - 1).fork
        v2 <- fiber2.join
        v1 <- fiber1.join
      } yield v1 + v2
    }

  // =================================================================================================================

  val error: Task[String] = ZIO.fail(new RuntimeException("Some Error"))
  val errorEither = error.either //  ZIO[Any, Nothing, Either[Throwable, String]]

  // =================================================================================================================

  val dt = for {
    fiber <- Clock.currentDateTime
      .debug
      //.flatMap(time => printLine(time))
      .schedule(Schedule.fixed(1.seconds))
      .uninterruptible
      .fork
    _ <- fiber.interrupt // Runtime stuck here and does not go further
  } yield ()

  // =================================================================================================================

  val dt2 = for {
    fiber <- printLine("Working on the first job")
      .schedule(Schedule.fixed(1.seconds))
      .ensuring {
        (printLine(
          "Finalizing or releasing a resource that is time-consuming"
        ) *> ZIO.sleep(7.seconds)).orDie
      }
      .fork
    _ <- fiber.interrupt.delay(4.seconds)
    _ <- printLine(
      "Starting another task when the interruption of the previous task finished"
    )
  } yield ()

  // =================================================================================================================

  // =================================================================================================================

  // =================================================================================================================



  def run = dt2

}
