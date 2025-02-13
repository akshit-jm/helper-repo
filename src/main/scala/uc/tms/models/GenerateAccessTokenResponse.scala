package uc.tms.models

case class GenerateAccessTokenResponse(customerId: String,
                                       scopeKey: String,
                                       tokenDetail: Option[TokenDetail] = None,
                                       encryptedAccessToken: Option[String] = None,
                                       status: String,
                                       isSuccess: Boolean)
