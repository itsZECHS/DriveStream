package zechs.drive.stream.utils.util

object UrlValidator {

    fun startsWithHttpOrHttps(url: String): Boolean {
        return url.trim().matches(Regex("^(http|https)://.*$"))
    }

}