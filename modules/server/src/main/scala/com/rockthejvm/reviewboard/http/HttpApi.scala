object HttpApi {
  // TODO Gather routes
  def gatherRoutes(controllers: List[BaseController]) =
    controllers.flatMap(_.routes)
  def makeControllers = for {
    health    <- HealthController.makeZIO
    companies <- CompanyController.makeZIO
  } yield List(health, companies)

  val endpointsZIO = makeControllers.map(gatherRoutes)

}
