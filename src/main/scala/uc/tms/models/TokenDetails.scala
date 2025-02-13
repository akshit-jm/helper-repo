package uc.tms.models

case class DaoCustomerTokenInfo(customerID: String,
                                tokenDetails: List[DaoTokenDetail])

case class DaoTokenDetail(id: Option[String],
                          scopeKey: String,
                          tokenTarget: String,
                          tokenType: String,
                          metadataIdentifierType: Option[String] = None,
                          metadataIdentifierValue: Option[String] = None,
                          isDeleted: Option[Boolean] = None,
                          encryptedTokenPayload: String,
                          createdTimestamp: String,
                          updatedTimestamp: String)

case class TokenDetail(
                        id: String,
                        scopeKey: String,
                        tokenTarget: String,
                        tokenType: String,
                        metadataIdentifierType: Option[String] = None,
                        metadataIdentifierValue: Option[String] = None,
                        isDeleted: Option[Boolean] = Some(false),
                        createdTimestamp: String,
                        updatedTimestamp: String)
