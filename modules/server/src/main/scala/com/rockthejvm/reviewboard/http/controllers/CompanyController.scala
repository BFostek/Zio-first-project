import zio.*
import collection.mutable
import sttp.tapir.server.ServerEndpoint

class CompanyController private extends BaseController with CompanyEndpoints {
  val db = mutable.Map[Long, Company]()
  // create
  val create: ServerEndpoint[Any, Task] = createEndpoint.serverLogicSuccess { req =>
    ZIO.succeed {
      val newId      = db.keys.max + 1
      val newcompany = req.toCompany(newId)
      db += (newId -> newcompany)
      newcompany
    }
  }

  val getAll: ServerEndpoint[Any, Task] =
    getAllEndpoint.serverLogicSuccess { _ => ZIO.succeed(db.values.toList) }

  val getById: ServerEndpoint[Any, Task] = getByIdEndpoint.serverLogicSuccess { idx =>
    ZIO.attempt(idx.toLong).map(db.get)
  }

  val routes: List[ServerEndpoint[Any, Task]] = List(create, getAll, getById)
}

object CompanyController {
  val makeZIO = ZIO.succeed(new CompanyController)
}
