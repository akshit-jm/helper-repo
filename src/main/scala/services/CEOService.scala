package services

import io.circe.syntax.EncoderOps
import sttp.client3.{HttpURLConnectionBackend, Identity, SttpBackend, UriContext, basicRequest}
import io.circe.generic.auto._
import uc.ceo.models.{EmailAsyncRequest, EmailCustomerInfo}

object CEOService {

  implicit val backend: SttpBackend[Identity, Any] = HttpURLConnectionBackend()

  def requestFetchEmail(accessInfo: EmailCustomerInfo) = {
    val statementAccessInfoOption = accessInfo.emailAccessInfoList.find(_.product == "CC_STATEMENT")
    statementAccessInfoOption.map { statementAccessInfo =>
      val requestData = EmailAsyncRequest(
        customerId = accessInfo.customerId,
        product = "CC_TRANSACTIONS",
        currentAttempt = 1,
        metadata = Map(), // empty map as per the original curl
        maxRetries = 3,
        emailIdentifier = statementAccessInfo.emailIdentifier
      )

      // Build the request
      val request = basicRequest
        .post(uri"http://localhost:13016/customer-email-orchestrator/customer/fetch-emails")
        .contentType("application/json")
        .body(requestData.asJson.noSpaces) // Convert the case class to JSON string

      // Send the request using the sttp client
      val response = request.send(backend)

      // Handle the response
      response.body match {
        case Right(body) =>
          body
        case Left(ex) =>
          println(s"Request failed with exception: $ex")
      }
    }

  }

}
