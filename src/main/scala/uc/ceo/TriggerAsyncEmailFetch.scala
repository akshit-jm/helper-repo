package uc.ceo

import client.ddb.DDBClient
import io.circe.generic.auto._
import io.circe.jawn.decode
import io.circe.syntax.EncoderOps
import services.CEOService
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import sttp.client3.{HttpURLConnectionBackend, Identity, SttpBackend, UriContext, basicRequest}
import uc.ceo.models.{EmailAsyncRequest, EmailCustomerInfo}
import utilities.DDBJsonUtils.AttributeValueEncoderDecoder._
import uc.ceo.utils.Utils
import utilities.FileUtils

import scala.util.Try

object TriggerAsyncEmailFetch extends App {

  private case class GetUserData(customerId: String)
  implicit val backend: SttpBackend[Identity, Any] = HttpURLConnectionBackend()

  def doWork(): Unit = {
    val tableName = Utils.getDDBTable
    val ddbClient = new DDBClient(tableName)
    var i = 1

    val fileData = FileUtils.readDataFromFile("/Users/akshitbansal/Developer/personal/playground/scala/HelperRepo/uploads/ceo/fetch_users_data.txt")
    fileData.foreach { userId =>
      i+=1
      val request = GetUserData(userId)
      val jsonString = request.asJson.noSpaces
      val map = decode[java.util.Map[String, AttributeValue]](jsonString) match {
        case Right(item) => item
        case Left(e) => throw new RuntimeException(s"Error parsing JSON: $e")
      }
      val item = ddbClient.getItem(map).get
      val emailAccessInfo = decode[EmailCustomerInfo](item) match {
        case Right(emailAccessInfo) => emailAccessInfo
        case Left(e) => throw e
      }

      CEOService.requestFetchEmail(emailAccessInfo)
      Try(Thread.sleep(500))
      println(s"Triggered till $i")
    }
  }

  doWork()
}
