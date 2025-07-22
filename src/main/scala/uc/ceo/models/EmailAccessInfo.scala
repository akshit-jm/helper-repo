package uc.ceo.models

import java.time.Instant

// Case class for EmailAccessInfo
case class EmailAccessInfo(
                            access: Boolean,
//                            createdTimestamp: Instant,
                            emailIdentifier: String,
//                            offsetTimestamp: Option[Instant],
                            product: String,
//                            updatedTimestamp: Instant
                          )

// Case class for the main object
case class EmailCustomerInfo(
                         customerId: String,
                         emailAccessInfoList: List[EmailAccessInfo]
                       )

