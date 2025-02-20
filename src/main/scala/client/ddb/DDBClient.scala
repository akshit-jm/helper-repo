package client.ddb

import io.circe.jawn.decode
import io.circe.syntax.EncoderOps
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model._
import utilities.DDBJsonUtils.AttributeValueEncoderDecoder._
import utilities.Utils

import scala.jdk.CollectionConverters._


class DDBClient(tableName: String) {

  private val profile = Utils.getProfile
  private val dynamoDbClient = DynamoDbClient.builder()
    .region(Region.AP_SOUTH_1) // Specify the region
    .credentialsProvider(ProfileCredentialsProvider.create(profile))
    .build()


  def batchUpdate(items: List[String]): Unit = {
    val putRequests = items.map { item =>
      val attMap = decode[java.util.Map[String, AttributeValue]](item) match {
        case Left(e) => throw e
        case Right(attMap) => attMap
      }
      WriteRequest.builder().putRequest(
        PutRequest.builder().item(attMap).build()
      ).build()
    }

    // Create the BatchWriteItem request
    val batchWriteRequest = BatchWriteItemRequest.builder()
      .requestItems(Map(tableName -> putRequests.asJava).asJava)
      .build()

    try {
      // Execute the batch write request
      val response = dynamoDbClient.batchWriteItem(batchWriteRequest)

      // Check for unprocessed items
      if (response.hasUnprocessedItems) {
        println("Some items were not processed. Retrying...")

        // Handle retries if necessary (for simplicity, we just log it here)
        val unprocessedItems = response.unprocessedItems()
        println(s"Unprocessed items: ${unprocessedItems}")
      } else {
        println(s"Batch write successful! ${items.length} items written.")
      }
    } catch {
      case e: Exception =>
        println(s"Error performing batch write: ${e.getMessage}")
    }
  }

  def scanItems(): Iterator[String] = new Iterator[String] {
    var lastEvaluatedKey: Map[String, AttributeValue] = null
    var items: List[String] = List()
    var currentPageIndex: Int = 0

    // Loop to get the next item when needed
    override def hasNext: Boolean = {
      if (currentPageIndex < items.length) {
        true
      } else {
        fetchNextPage()
        currentPageIndex < items.length
      }
    }

    override def next(): String = {
      if (!hasNext) {
        throw new NoSuchElementException("No more users.")
      }

      val user = items(currentPageIndex)
      currentPageIndex += 1
      user
    }

    // Function to fetch the next page of data synchronously
    private def fetchNextPage(): Unit = {
      val scanRequest = ScanRequest.builder()
        .tableName(tableName)
        .exclusiveStartKey(lastEvaluatedKey.asJava)
        .build()

      try {
        val scanResponse = dynamoDbClient.scan(scanRequest)
        items = scanResponse.items().asScala.toList.map(item => {
          item.asJson.toString()
        })

        // Update the LastEvaluatedKey for pagination
        lastEvaluatedKey = Option(scanResponse.lastEvaluatedKey().asScala.toMap).orNull

        // Reset the page index to 0 when a new page of users is fetched
        currentPageIndex = 0

      } catch {
        case e: Exception =>
          println(s"Error scanning DynamoDB: ${e.getMessage}")
          items = List() // In case of an error, ensure that no users are returned
      }
    }
  }

}
