package zechs.drive.stream.utils.util

object GoogleClientValidator {

    fun isValidClientId(clientId: String?): Boolean {
        if (clientId.isNullOrBlank()) return false
        return clientId.endsWith(".apps.googleusercontent.com")
    }

}