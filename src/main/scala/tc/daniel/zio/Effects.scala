package tc.daniel.zio

import scala.concurrent.Future

object Effects {

  // functional programming
  // EXPRESSIONS
  def combine(a: Int, b: Int): Int = a + b

  // local reasoning = type signature describes the kind of computation that will be performed
  // referential transparency
  val five = combine(2, 3)
  val five_v2 = 2 + 3
  val five_v3 = 5

  // not all expressions are RT
  // example 1: printing
  val resultOfPrinting: Unit = println("Learning ZIO")
  val resultOfPrinting_v2: Unit = ()

  // example 2: changing a variable
  var anInt = 0
  val changingInt: Unit = (anInt = 42) // side effect
  val changingInt_v2: Unit = ()

  // side effects are inevitable
  /*
    Effect desires/properties
      - the type signature describes what KIND of computation it will perform
      - the type signature describes the type of VALUE that will be produced
      - if side effects are required, construction must be separate from the EXECUTION
   */

  /*
    Example 1: Option[A] = possibly absent values
      - type signature describes the kind of computation: a possibly absent value
      - also describes the value of computation: produces a value of type A
      - no side effects are needed
   */
  val anOption: Option[Int] = Option(32)

  /*
    Example 2: Future[A]
      - describes an asynchronous computation
      - produces a value of type A
      - side effects are required, but construction is NOT SEPARATE from execution

      => Future is NOT an EFFECT (in OUR definition)
   */
  import scala.concurrent.ExecutionContext.Implicits.global
  val aFuture = Future(32)

  /*
    Example 3: MyIO[A]
      - describes any kind of computation (which might perform side effects)
      - produces values of type A if the computation is successful
      - side effects are required, but construction is SEPARATE from execution
   */
  case class MyIO[A](unsafeRun: () => A) {
    def map[B](f: A => B): MyIO[B] =
      MyIO(() => f(unsafeRun()))
    def flatMap[B](f: A => MyIO[B]): MyIO[B] =
      MyIO(() => f(unsafeRun()).unsafeRun())
  }

  val anIOWithSideEffects = MyIO { () =>
    println("Blabla")
    42
  }

  // My own exercise. ******  BEWARE !!!! *********
  class MyIO2[A](unsafeRun: => A) {
//    def unsafeRun: A = unsafeRun
    def map[B](f: A => B): MyIO2[B] =
      new MyIO2(f(unsafeRun))
//    def flatMap[B](f: A => MyIO2[B]) =
//      new MyIO2(f(unsafeRun).unsafeRun)
  }


  def main(args: Array[String]): Unit = {
    anIOWithSideEffects.unsafeRun()

  }

}
