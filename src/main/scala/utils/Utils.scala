package utils

object Utils {

  def getProfile: String = sys.env.getOrElse("AWS_PROFILE", "staging")

}
