import zio.*
import collection.mutable
import sttp.tapir.server.ServerEndpoint

class CompanyController private (service: CompanyService)
    extends BaseController
    with CompanyEndpoints {
  val db = mutable.Map[Long, Company]()
  // create
  val create: ServerEndpoint[Any, Task] = createEndpoint.serverLogicSuccess { req =>
    service.create(req)
  }

  val getAll: ServerEndpoint[Any, Task] =
    getAllEndpoint.serverLogicSuccess { _ =>
      service.getAll
    }

  val getById: ServerEndpoint[Any, Task] = getByIdEndpoint.serverLogicSuccess { idx =>
    ZIO.attempt(idx.toLong).flatMap(service.getById).catchSome { case _: NumberFormatException =>
      service.getBySlug(idx)
    }
  }

  val routes: List[ServerEndpoint[Any, Task]] = List(create, getAll, getById)
}

object CompanyController {
  val makeZIO = for {
    service <- ZIO.service[CompanyService]
  } yield new CompanyController(service)
}
