package uc.ceo.utils

object Utils {

  def getDDBTable: String = {
    if (utilities.Utils.getProfile == "dsprod") {
      "jm-dsprod-customer-email-access-infoset"
    } else {
      "jm-staging-customer-email-access-infoset"
    }
  }
}
