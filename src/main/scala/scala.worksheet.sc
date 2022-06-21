import zio.Task
import zio.ZIO
import zio.{IO, Task, ZIO}

import scala.concurrent.Future
import scala.io.StdIn
import scala.util.Using

/*
  type IO[+E, +A]   = ZIO[Any, E, A]         // Succeed with an `A`, may fail with `E`        , no requirements.
  type Task[+A]     = ZIO[Any, Throwable, A] // Succeed with an `A`, may fail with `Throwable`, no requirements.
  type RIO[-R, +A]  = ZIO[R, Throwable, A]   // Succeed with an `A`, may fail with `Throwable`, requires an `R`.
  type UIO[+A]      = ZIO[Any, Nothing, A]   // Succeed with an `A`, cannot fail              , no requirements.
  type URIO[-R, +A] = ZIO[R, Nothing, A]     // Succeed with an `A`, cannot fail              , requires an `R`.
 */

val a = ZIO.attempt("")
val readLine = ZIO.attempt(StdIn.readLine())

import java.io.IOException

val readLine2: IO[IOException, String] =
  readLine.refineToOrDie[IOException]

def printLine(line: String) = ZIO.succeed(println(line))

val echo = for {
  line <- readLine
  _ <- printLine(line)
} yield ()

val fail = ZIO.fail("")
val succeed = ZIO.succeed("")

val zoption = ZIO.fromOption(Some(2)) // : IO[Option[Nothing], Int]
val zoption2 = zoption.mapError(_ => "It wasn't there") // IO[String, Int]

case class User(id: String, teamId: String)

case class Team(id: String)

val maybeId: IO[Option[Nothing], String] = ZIO.fromOption(Some("abc123"))
def getUser(userId: String): IO[Throwable, Option[User]] = ???
def getTeam(teamId: String): IO[Throwable, Team] = ???

val result: IO[Throwable, Option[(User, Team)]] = (for {
  id <- maybeId
  user <- getUser(id).some
  team <- getTeam(user.teamId).asSomeError
} yield (user, team)).unsome

val zeither = ZIO.fromEither(Right("Success!"))

import scala.util.Try

val ztry = ZIO.fromTry(Try(42 / 0))

def userByIdAsync(id: Int)(cb: Option[String] => Unit): Unit = ???

def getUserById(id: Int): ZIO[Any, Option[String], String] =
  ZIO.async { (callback: ZIO[Any, Option[String], String] => Unit) =>
    userByIdAsync(id) {
      case Some(name) => callback(ZIO.succeed(name))
      case None       => callback(ZIO.fail(None))
    }
  }

val future = Future.successful(10)
val zfuture = ZIO.fromFuture { implicit ec =>
  future.map(_ => "Goodbye!")
}

val sleeping = ZIO.attemptBlocking(Thread.sleep(Long.MaxValue))

import java.net.ServerSocket
import zio.UIO
def accept(l: ServerSocket) =
  ZIO.attemptBlockingCancelable(l.accept())(ZIO.succeed(l.close()))

import scala.io.{Codec, Source}

def download(url: String) =
  ZIO.attempt {
    Using(Source.fromURL(url)(using Codec.UTF8)) { reader =>
      reader.mkString
    }
  }

def safeDownload(url: String) = ZIO.blocking(download(url))

val succeeded: UIO[Int] = ZIO.succeed(21).map(_ * 2)
val failed = ZIO.fail("No no!").mapError(msg => new Exception(msg))

// val readLine = ZIO.attempt(StdIn.readLine())
// def printLine(line: String) = ZIO.succeed(println(line))

val sequenced = readLine.flatMap(input => printLine(s"You entered: $input"))

val program = // : ZIO[Any, Throwable, Unit]
  for {
    _ <- printLine("Hello! What is your name?")
    name <- readLine
    _ <- printLine(s"Hello, ${name}, welcome to ZIO!")
  } yield ()

val zipped: UIO[(String, Int)] =
  ZIO.succeed("4").zip(ZIO.succeed(2))

val zipRight1 =
  printLine("What is your name?").zipRight(readLine)

val zipRight2 =
  printLine("What is your name?") *> readLine

val zeitherAgain: UIO[Either[String, Int]] =
  ZIO.fail("Uh oh!").either

def sqrt(io: UIO[Double]): IO[String, Double] =
  ZIO.absolve(
    io.map(value =>
      if (value < 0.0) Left("Value must be >= 0.0")
      else Right(Math.sqrt(value))
    )
  )

import java.io.FileNotFoundException
def openFile(str: String) = ZIO.attempt { Array[Byte](0, 0) }

val z = openFile("primary.json").catchAll(_ => openFile("backup.json"))

val data =
  openFile("primary.data").catchSome { case _: FileNotFoundException =>
    openFile("backup.data")
  }

val primaryOrBackupData =
  openFile("primary.data").orElse(openFile("backup.data"))

lazy val DefaultData: Array[Byte] = Array(0, 0)

val primaryOrDefaultData: UIO[Array[Byte]] =
  openFile("primary.data").fold(_ => DefaultData, data => data)

val primaryOrSecondaryData =
  openFile("primary.data").foldZIO(
    _ => openFile("secondary.data"),
    data => ZIO.succeed(data)
  )

import zio.Schedule
val retriedOpenFile = openFile("primary.data").retry(Schedule.recurs(5))

openFile("primary.data").retryOrElse(
  Schedule.recurs(5),
  (_, _) => ZIO.succeed(DefaultData)
)

val finalizer =
  ZIO.succeed(println("Finalizing!"))

val finalized: IO[String, Unit] =
  ZIO.fail("Failed!").ensuring(finalizer)

def fib(n: Long): UIO[Long] = ZIO.succeed {
  if (n <= 1) ZIO.succeed(n)
  else fib(n - 1).zipWith(fib(n - 2))(_ + _)
}.flatten

import zio.Fiber
val fib100Fiber: UIO[Fiber[Nothing, Long]] =
  for {
    fiber <- fib(100).fork
  } yield fiber

  