package services

import io.circe.Json
import io.circe.generic.auto._
import io.circe.jawn.decode
import io.circe.syntax.EncoderOps
import sttp.client3.{HttpURLConnectionBackend, Identity, SttpBackend, UriContext, basicRequest}
import uc.tms.models.GenerateAccessTokenResponse

object TokenManagementService {

  implicit val backend: SttpBackend[Identity, Any] = HttpURLConnectionBackend()

  def getAccessToken(userId: String, tokenId: String) = {
    // Define the URL for the API
    val url = s"http://localhost:9011/token-management/customerId/$userId/scopeKey/google-oauth-scope-gmail-read/access-token"
    val payload: Json = Json.obj(
      "tokenId" -> tokenId.asJson
    )

    // Define the headers
    val headers = Seq(
      "Content-Type" -> "application/json"
    ).toMap

    // Create a request
    val request = basicRequest
      .post(uri"$url")
      .headers(headers)
      .body(payload.noSpaces)

    // Send the request and get the response
    val response = request.send(backend)

    // Print the response body
    response.body match {
      case Right(responseBody) =>
        val accessToken = decode[GenerateAccessTokenResponse](responseBody) match {
          case Left(error) =>
            print("Decoding error", error)
            None
          case Right(accessTokenResponse) => Some(accessTokenResponse)
        }
        accessToken
      case Left(error) =>
        println(s"API error: $error")
        None
    }
  }
}
