import zio.*
import sttp.tapir.*
import sttp.tapir.server.ziohttp.ZioHttpServerOptions
import zio.http.Server
import sttp.tapir.server.ziohttp.{ZioHttpInterpreter, ZioHttpServerOptions}

object Application extends ZIOAppDefault {

  val serverProgram = for {
    controller <- HealthController.makeZIO
    server <- Server.serve(
      ZioHttpInterpreter(
        ZioHttpServerOptions.default
      ).toHttp(controller.health)
    )
  } yield ()
  override def run: ZIO[Any & (ZIOAppArgs & Scope), Any, Any] = serverProgram.provide(
    Server.default
  )
}
