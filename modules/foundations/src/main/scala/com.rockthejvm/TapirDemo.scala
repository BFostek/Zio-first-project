import zio.*
import sttp.tapir.*
import zio.http.Server
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.server.ziohttp.ZioHttpServerOptions
object TapirDemo extends ZIOAppDefault {
  val simplestEndpoint = endpoint
    .tag("simple")
    .name("simple")
    .description("simplest endpoint possible")
    .get          // http method
    .in("simple") // path
    .out(plainBody[String])
    .serverLogicSuccess[Task](_ => ZIO.succeed("All good"))

  val simpleServerProgram = Server.serve(
    ZioHttpInterpreter(
      ZioHttpServerOptions.default
    ).toHttp(simplestEndpoint)
  )
  override def run = simpleServerProgram.provide(
    Server.default
    )
}
