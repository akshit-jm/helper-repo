package services

import io.circe.syntax.EncoderOps
import io.circe.generic.auto._
import sttp.client3.{HttpURLConnectionBackend, Identity, SttpBackend, UriContext, basicRequest}
import sttp.model.MediaType
import uc.mm.models.UserDeleteDataRequest

object MMService {

  implicit val backend: SttpBackend[Identity, Any] = HttpURLConnectionBackend()

  def deleteUserTransactions(deleteDataRequest: UserDeleteDataRequest) = {

    // Define the URL and payload
    val url = s"http://localhost:8001/mm/internal/transactions/${deleteDataRequest.userId}"

    // Define the payload data structure
    val payload = Map[String, String](
      "product" -> deleteDataRequest.product,
      "from" -> deleteDataRequest.startDate,
      "to" -> deleteDataRequest.endDate,
      "accountID" -> deleteDataRequest.accountId.orNull
    )

    // Create a POST request
    val request = basicRequest
      .body(payload.asJson.noSpaces) // Convert payload to JSON (use Circe library for encoding)
      .contentType(MediaType.ApplicationJson)
      .post(uri"$url")

    // Execute the request
    val response = request.send(backend)

    // Print the response body
    response.body match {
      case Right(body) => println(body)
      case Left(error) => println(s"Error: $error")
    }
  }

}
