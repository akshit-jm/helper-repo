package services

import io.circe.syntax.EncoderOps
import io.circe.generic.auto._
import sttp.client3.{HttpURLConnectionBackend, Identity, SttpBackend, UriContext, basicRequest}
import sttp.model.MediaType
import uc.mm.models.UserDeleteDataRequest

import java.time.ZoneId

object MMService {

  implicit val backend: SttpBackend[Identity, Any] = HttpURLConnectionBackend()

  def deleteUserTransactions(deleteDataRequest: UserDeleteDataRequest): Either[String, String] = {

    // Define the URL and payload
    val url = s"http://localhost:13004/mm/internal/transactions/${deleteDataRequest.user_id}?mmUUID=${deleteDataRequest.transactionuniqueidentifer}"
    val date = deleteDataRequest.transactiondatetime.atZone(ZoneId.of("Asia/Kolkata")).toLocalDate.toString

    // Define the payload data structure
    val payload = Map(
      "product" -> "ADA",
      "from" -> date,
      "to" -> date
    )

    // Create a DELETE request
    val request = basicRequest
      .body(payload.asJson.noSpaces) // Convert payload to JSON (use Circe library for encoding)
      .contentType(MediaType.ApplicationJson)
      .delete(uri"$url")

    // Execute the request
    val response = request.send(backend)

    // Handle the response
    response.body match {
      case Right(body) =>
        if (body == "0") {
          Left(s"Not deleted: $deleteDataRequest")
        } else {
          Right(s"Deleted: $deleteDataRequest")
        }
      case Left(error) => Left(s"Error: $error")
    }
  }

}
