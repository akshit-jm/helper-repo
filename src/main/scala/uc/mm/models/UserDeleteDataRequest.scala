package uc.mm.models

import java.time.LocalDate

case class UserDeleteDataRequest(
  userId: String,
  product: String,
  accountId: Option[String],
  startDate: String, // 2024-01-01
  endDate: String,
)
