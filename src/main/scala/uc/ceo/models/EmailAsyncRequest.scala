package uc.ceo.models

case class EmailAsyncRequest(
                         customerId: String,
                         product: String,
                         currentAttempt: Int,
                         metadata: Map[String, String], // Assuming metadata is a map of string keys and values
                         maxRetries: Int,
                         emailIdentifier: String
                       )
