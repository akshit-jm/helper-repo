package uc.mm

import io.circe.generic.auto._
import io.circe.jawn.decode
import services.MMService
import uc.mm.models.UserDeleteDataRequest
import utilities.FileUtils

import java.util.concurrent.Executors
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

object DeleteMMTransactions extends App {

  private val threadPool = Executors.newFixedThreadPool(15)
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutor(threadPool)

  def doWork(): Unit = {
    val filePath = "/Users/akshitbansal/Developer/personal/playground/py/uploads/streamed_data.json"
    val userDataToDelete = FileUtils.readDataFromFile(filePath)
    val dataToDelete = decode[List[UserDeleteDataRequest]](userDataToDelete.head) match {
      case Left(e) => throw e
      case Right(v) => v
    }
    val filteredTransactions = dataToDelete.slice(210000, dataToDelete.length)
    println(s"Total transactions to process: ${filteredTransactions.length}")

    println(filteredTransactions.slice(0, 10))

    // Using Futures to process the transactions concurrently with a limit of 10 concurrent threads
//    val futures = filteredTransactions.zipWithIndex.map { case (data, index) =>
//      Future {
//        MMService.deleteUserTransactions(data)
//        if ((index + 1) % 2000 == 0) {
//          println(s"Processed $index transactions")
//        }
//      }
//    }
//
//    // Wait for all futures to complete
//    val aggregatedFuture = Future.sequence(futures).recover {
//      case e: Exception => throw e
//    }
//
//    // Handle completion
//    aggregatedFuture.onComplete {
//      case Success(_) => println("All transactions have been processed.")
//      case Failure(exception) => println(s"An error occurred: ${exception.getMessage}")
//    }
//
//    // Block the main thread to wait for all Futures to complete
//    Await.result(aggregatedFuture, Duration.Inf)
//
//    // Shut down the thread pool after completion
//    threadPool.shutdown()
  }

  doWork()

}
