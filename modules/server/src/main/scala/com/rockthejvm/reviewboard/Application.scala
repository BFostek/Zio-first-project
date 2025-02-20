import zio.*
import sttp.tapir.*
import sttp.tapir.server.ziohttp.ZioHttpServerOptions
import zio.http.Server
import sttp.tapir.server.ziohttp.{ZioHttpInterpreter, ZioHttpServerOptions}

object Application extends ZIOAppDefault {
  val helthEndpoint = endpoint
    .tag("health")
    .name("health")
    .description("health check")
    .get
    .in("health")
    .out(plainBody[String])
    .serverLogicSuccess[Task](_ => ZIO.succeed("All good!"))

  val serverProgram = Server.serve(
    ZioHttpInterpreter(
      ZioHttpServerOptions.default
    ).toHttp(helthEndpoint)
  )
  override def run: ZIO[Any & (ZIOAppArgs & Scope), Any, Any] = Console.printLine("Olá mundo!")
}
