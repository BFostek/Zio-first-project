import zio.*
import sttp.tapir.*
import sttp.tapir.server.ziohttp.ZioHttpServerOptions
import zio.http.Server
import sttp.tapir.server.ziohttp.{ZioHttpInterpreter, ZioHttpServerOptions}

object Application extends ZIOAppDefault {

  val serverProgram = for {
    endpoints <- HttpApi.endpointsZIO
    server <- Server.serve(
      ZioHttpInterpreter(
        ZioHttpServerOptions.default
      ).toHttp(endpoints)
    )
  } yield ()
  override def run: ZIO[Any & (ZIOAppArgs & Scope), Any, Any] = serverProgram.provide(
    Server.default
  )
}
