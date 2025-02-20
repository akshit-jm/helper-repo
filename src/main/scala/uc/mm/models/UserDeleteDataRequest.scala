package uc.mm.models

import java.time.{Instant, LocalDate}

case class UserDeleteDataRequest(
                                  user_id: String,
                                  transactionuniqueidentifer: String,
                                  transactiondatetime: Instant
)
