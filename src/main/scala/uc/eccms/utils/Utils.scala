package uc.eccms.utils

object Utils {

  def getLinesTable: String = {
    if (utilities.Utils.getProfile == "pfm-prod") {
      "pfm-prod-ecc-credit-lines"
    } else {
      "jm-staging-ecc-credit-lines"
    }
  }

  def getBillsTable: String = {
    if (utilities.Utils.getProfile == "pfm-prod") {
      "pfm-prod-ecc-credit-lines"
    } else {
      "jm-staging-ecc-credit-lines"
    }
  }
}
