import zio.json.JsonCodec
import zio.json.DeriveJsonCodec
final case class CreateCompanyRequest(
    name: String,
    url: String,
    location: Option[String] = None,
    country: Option[String] = None,
    industry: Option[String] = None,
    image: Option[String] = None,
    tags: List[String] = List()
) {
  def toCompany(id: Long) =
    Company(id, Company.makeSlug(name), name, url, location, country, industry, image, tags)
}

object CreateCompanyRequest {
  given codec: JsonCodec[CreateCompanyRequest] = DeriveJsonCodec.gen[CreateCompanyRequest]
}
