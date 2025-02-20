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

  // Error handling
  val anAttempt: ZIO[Any, Throwable, Int] = ZIO.attempt {
    println("Trying something")
    val string: String = null
    string.length
  }
  // catch errors effectfully
  val catchError = anAttempt.catchAll(e => ZIO.succeed(s"Returning some different value"))
  val catchSelective = anAttempt.catchSome {
    case e: RuntimeException => ZIO.succeed(s"Ignoring runtime exception: $e")
    case _                   => ZIO.succeed("Ignoring everything else")
  }

  // fibers
  //
  val delayedValue = ZIO.sleep(1.second) *> Random.nextIntBetween(0, 100)
  val aPair = for {
    a <- delayedValue
    b <- delayedValue
  } yield (a, b) // it takes 2 seconds
  val aPairPar = for {
    fibA <- delayedValue.fork
    fibB <- delayedValue.fork
    a    <- fibA.join
    b    <- fibB.join
  } yield (a, b) // this takes 1 second

  val interruptedFiber = for {
    fib <- delayedValue.onInterrupt(ZIO.succeed(println("I'm interrupted!"))).fork
    _   <- ZIO.sleep(500.millis) *> ZIO.succeed(println("canceling fiber")) *> fib.interrupt
    _   <- fib.join

  } yield ()

  val ignoredInterruption = for {
    fib <- ZIO
      .uninterruptible(delayedValue.onInterrupt(ZIO.succeed(println("I'm interrupted!"))))
      .fork
    _ <- ZIO.sleep(500.millis) *> ZIO.succeed(println("canceling fiber")) *> fib.interrupt
    _ <- fib.join

  } yield ()

  // many APIs on top of fibers
  val aPairPar_v2 = delayedValue.zipPar(delayedValue)
  val randomx10   = ZIO.collectAllPar((1 to 10).map(_ => delayedValue)) // "Transverse"
  // reduceAllPar, mergeAllPar, foreachPar
  //
  // dependencies
  case class User(name: String, email: String)
  class UserSubscription(emailService: EmailService, userDatabase: UserDatabase) {
    def subscribeUser(user: User): Task[Unit] =
      ZIO.succeed(s"subscribed $user") *> emailService.email(user)
  }
  class EmailService {
    def email(user: User): Task[Unit] = ZIO.succeed(s"Emailed $user")
  }
  class UserDatabase(connectionPool: ConnectionPool)
  class ConnectionPool(nConnections: Int)
  case class Connection()
  override def run = ignoredInterruption
}
