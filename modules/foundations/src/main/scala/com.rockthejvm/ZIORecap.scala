import zio.*
import scala.io.StdIn

object ZIORecap extends ZIOAppDefault {
  // ZIO = Data structure describing arbitrary computations (including side effects)
  // "effects" = computations as values
  //
  //  basics
  val meaningOfLife: ZIO[Any, Nothing, Int] = ZIO.succeed(42)
  // fail
  val aFailure: ZIO[Any, String, Nothing] = ZIO.fail("Something went wrong")
  // suspension/delay
  val aSuspension: ZIO[Any, Throwable, Int] = ZIO.suspend(meaningOfLife)

  // map/flatmap
  val improveMOL = meaningOfLife.map(_ * 2)
  val printMOL   = meaningOfLife.flatMap(mol => ZIO.succeed(println(mol)))

  val smallProgram = for {
    _    <- Console.printLine("What's your name?")
    name <- ZIO.succeed(StdIn.readLine())
    _    <- Console.printLine(s"Welcome to ZIO, $name")
  } yield ()
  override def run = smallProgram
}
