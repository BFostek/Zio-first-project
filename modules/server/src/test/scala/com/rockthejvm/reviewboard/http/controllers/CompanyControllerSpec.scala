import zio.*
import zio.test.*
import sttp.tapir.server.stub.TapirStubInterpreter
import sttp.client3.testing.SttpBackendStub
import sttp.monad.MonadError
import sttp.tapir.ztapir.RIOMonadError
import sttp.client3.*
import sttp.tapir.generic.auto.*
import zio.json.*
object CompanyControllerSpec extends ZIOSpecDefault {
  private given zioMonadError: MonadError[Task] = new RIOMonadError[Any]
  private val serviceStub = new CompanyService {
    override def create(req: CreateCompanyRequest): Task[Company] = ???
    override def getAll: Task[List[Company]]                      = ???
    override def getBySlug(slug: String): Task[Option[Company]]   = ???
    override def getById(id: Long): Task[Option[Company]]         = ???
  }

  override def spec: Spec[TestEnvironment & Scope, Any] = suite("CompanyControllerSpec")(
    test("post company") {
      // create the controller
      val program = for {
        controller <- CompanyController.makeZIO
        // build tapir backend
        backendStub <- ZIO.succeed(
          TapirStubInterpreter(SttpBackendStub(zioMonadError))
            .whenServerEndpointRunLogic(controller.create)
            .backend()
        )
        response <- basicRequest
          .post(uri"/companies")
          .body(CreateCompanyRequest("Rock the JVM", "rockthejvm.com").toJson)
          .send(backendStub)
      } yield response.body
      // // run http request

      assertZIO(program)(Assertion.assertion("inspect http response from getAll") { respBody =>
        println(respBody.toOption.flatMap(_.fromJson[Company].toOption))
        respBody.toOption
          .flatMap(_.fromJson[Company].toOption)
          .contains(Company(1, "rock-the-jvm", "Rock the JVM", "rockthejvm.com"))
      })
      // inspect http response
      //
    },
    test("get all") {
      val program = for {
        controller <- CompanyController.makeZIO
        // build tapir backend
        backendStub <- ZIO.succeed(
          TapirStubInterpreter(SttpBackendStub(zioMonadError))
            .whenServerEndpointRunLogic(controller.getAll)
            .backend()
        )
        response <- basicRequest
          .get(uri"/companies")
          .body()
          .send(backendStub)
      } yield response.body
      assertZIO(program)(Assertion.assertion("inspect http response from getAll") { respBody =>
        println(respBody.toOption.flatMap(_.fromJson[Company].toOption))
        respBody.toOption
          .flatMap(_.fromJson[List[Company]].toOption)
          .contains(List())
      })

    },
    test("get by id") {
      val program = for {
        controller <- CompanyController.makeZIO
        // build tapir backend
        backendStub <- ZIO.succeed(
          TapirStubInterpreter(SttpBackendStub(zioMonadError))
            .whenServerEndpointRunLogic(controller.getById)
            .backend()
        )
        response <- basicRequest
          .get(uri"/companies/1")
          .send(backendStub)
      } yield response.body
      assertZIO(program)(Assertion.assertion("inspect http response from get by id") { respBody =>
        respBody.toOption
          .flatMap(_.fromJson[Company].toOption)
          .isEmpty
      })

    }
  )
  .provide(ZLayer.succeed(serviceStub))

}
