package uc.tms.utils

object Utils {

  def getDDBTable: String = {
    if (utilities.Utils.getProfile == "dsprod") {
      "jm-dsprod-tms-customer-token-infoset"
    } else {
      "jm-staging-tms-customer-token-infoset"
    }
  }
}
