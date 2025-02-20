package services

import io.circe.syntax.EncoderOps
import io.circe.generic.auto._
import sttp.client3.{HttpURLConnectionBackend, Identity, SttpBackend, UriContext, basicRequest}
import sttp.model.MediaType
import uc.mm.models.UserDeleteDataRequest

import java.time.ZoneId

object MMService {

  implicit val backend: SttpBackend[Identity, Any] = HttpURLConnectionBackend()

  def deleteUserTransactions(deleteDataRequest: UserDeleteDataRequest) = {

    // Define the URL and payload
    val url = s"http://localhost:13004/mm/internal/transactions/${deleteDataRequest.user_id}?mmUUID=${deleteDataRequest.transactionuniqueidentifer}"

    val date = deleteDataRequest.transactiondatetime.atZone(ZoneId.of("Asia/Kolkata")).toLocalDate.toString
    // Define the payload data structure
    val payload = Map[String, String](
      "product" -> "ADA",
      "from" -> date,
      "to" -> date,
    )

    // Create a POST request
    val request = basicRequest
      .body(payload.asJson.noSpaces) // Convert payload to JSON (use Circe library for encoding)
      .contentType(MediaType.ApplicationJson)
//      .headers(Map("x-execute-delete" -> "true"))
      .delete(uri"$url")

    // Execute the request
    val response = request.send(backend)

    // Print the response body
    response.body match {
      case Right(body) =>
        if (body == "0") {
          println(s"Not deleted $deleteDataRequest")
        } else {
//          println(s"Deleted $deleteDataRequest")
        }
      case Left(error) => println(s"Error: $error")
    }
  }

}
