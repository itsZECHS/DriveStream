package zechs.drive.stream.data.model

import com.google.errorprone.annotations.Keep

@Keep
data class TokenRequestBody(
    val token: String
)
