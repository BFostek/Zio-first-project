// Business Logic
// in between the HTTP layer and the DB Layer
import zio.*
import collection.mutable
import sttp.tapir.server.ServerEndpoint

trait CompanyService {
  def create(req: CreateCompanyRequest): Task[Company]
  def getAll: Task[List[Company]]
  def getById(id: Long): Task[Option[Company]]
  def getBySlug(slug: String): Task[Option[Company]]
}
object CompanyService {
  val dummyLayer = ZLayer.succeed(new CompanyServiceDummy)
}

class CompanyServiceDummy extends CompanyService {
  val db = mutable.Map[Long, Company]()
  override def create(req: CreateCompanyRequest): Task[Company] = ZIO.succeed {
    val newId      = db.keys.maxOption.getOrElse(0L) + 1
    val newcompany = req.toCompany(newId)
    db += (newId -> newcompany)
    newcompany
  }

  override def getAll: Task[List[Company]]              = ZIO.succeed(db.values.toList)
  override def getById(id: Long): Task[Option[Company]] = ZIO.succeed(db.get(id))
  override def getBySlug(slug: String): Task[Option[Company]] =
    ZIO.succeed(db.values.find(_.slug == slug))

}
