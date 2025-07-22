package uc.tms

import client.ddb.DDBClient
import io.circe.generic.auto._
import io.circe.jawn.decode
import io.circe.syntax.EncoderOps
import services.TokenManagementService
import uc.tms.models.DaoCustomerTokenInfo
import utilities.FileUtils
import utils.{Utils}

import scala.collection.mutable.ListBuffer

object ScanAndRemoveInactive extends App {

  /**
   * This method processes customer token information stored in a DynamoDB table,
   * checks if each token requires an update based on the status of an access token,
   * and accumulates a list of token information objects that need to be updated.
   * Once accumulated, the method clears the list and prepares the data for updating.
   */
  private def doWork(): Unit = {
    // Initialize a DynamoDB client to interact with the table defined by `Utils.getDDBTable`.
    val ddbClient = new DDBClient(Utils.getDDBTable)

    // Scan all items in the DynamoDB table.
    val iterator = ddbClient.scanItems()
    val toUpdateList = new ListBuffer[DaoCustomerTokenInfo]()
    var emptyTokenIdInfos = new ListBuffer[DaoCustomerTokenInfo]()
    val toUpdateInfos = new ListBuffer[DaoCustomerTokenInfo]()

    // Loop through each item retrieved from the DynamoDB scan operation.
    while (iterator.hasNext) {
      // Retrieve the token information as a string from the iterator.
      val tokenInfoString = iterator.next()
      val tokenInfo = decode[DaoCustomerTokenInfo](tokenInfoString) match {
        case Left(err) => throw err
        case Right(e) => e
      }

      // Flag to track if this token info needs updating.
      var needsUpdate = false

      // Check if the token info should be processed.
      if (shouldProcessTokenInfo(tokenInfo)) {

        // Update the token details, checking for the access token's status.
        val updatedTokenDetails = tokenInfo.tokenDetails.flatMap { tokenDetail =>
          // Attempt to retrieve the access token for this token detail.
          val accessTokenResponse = TokenManagementService.getAccessToken(tokenInfo.customerID, tokenDetail.id.getOrElse(""))

          // Check if access token retrieval was successful and handle different status responses.
          accessTokenResponse.map(accessToken => {
            if ((accessToken.status == "UNAUTHORIZED" || accessToken.status == "NO_ACTIVE_TOKEN_FOUND") && (tokenDetail.id.isEmpty || tokenDetail.id.get.isEmpty)) {
              // If the status indicates unauthorized or no active token, mark it as needing an update and return None.
              emptyTokenIdInfos.addOne(tokenInfo)
              needsUpdate = true
              None
            } else {
              // If the token is valid, keep the token detail as is.
              Some(tokenDetail)
            }
          }).getOrElse(Some(tokenDetail)) // If no access token is returned, keep the original token detail.
        }
        // If the token info needs an update, create a new object with updated token details.
        if (needsUpdate) {
          val updatedTokenInfo = tokenInfo.copy(tokenDetails = updatedTokenDetails)
          toUpdateList.addOne(updatedTokenInfo)
          toUpdateInfos.addOne(updatedTokenInfo)
        }

        // If there are items to update, trigger the update operation and clear the update list.
        if (toUpdateList.length >= 20) {
          ddbClient.batchUpdate(toUpdateList.map(_.asJson.toString()).toList)
          toUpdateList.clear() // Clear the list after the update.
        }
      }
    }
    FileUtils.saveToFile("to_delete_2.json", toUpdateInfos.toList)
    FileUtils.saveToFile("empty_ids_2.json", emptyTokenIdInfos.toList)

  }


  private def shouldProcessTokenInfo(tokenInfo: DaoCustomerTokenInfo): Boolean = {
    val isGoodToken = tokenInfo.tokenDetails.forall(detail => {
      detail.metadataIdentifierValue.isDefined && detail.metadataIdentifierType.isDefined
    })

    !isGoodToken
  }

  doWork()
}
