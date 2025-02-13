package uc.mm

import services.MMService
import uc.mm.models.UserDeleteDataRequest
import utils.FileUtils

import java.time.LocalDate
import scala.util.Try

object DeleteMMTransactions extends App {

  def doWork() = {
    val startDate = LocalDate.now().toString
    val endDate = LocalDate.now().toString
    val product = "ADA"

    val filePath = getClass.getResource("./data/del_users.txt")
    val userDataToDelete = FileUtils.readDataFromFile(filePath)
    val dataToDelete = userDataToDelete.map(line => {
      val fields = line.split(",")
      val userId = fields(0)
      val accountId = Try(fields(1)).toOption
      UserDeleteDataRequest(userId, product, accountId, startDate, endDate)
    })

    dataToDelete.map(data => {
      MMService.deleteUserTransactions(data)
    })
  }

  doWork()

}
