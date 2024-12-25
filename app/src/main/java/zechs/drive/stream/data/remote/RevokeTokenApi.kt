package zechs.drive.stream.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import zechs.drive.stream.data.model.TokenRequestBody

interface RevokeTokenApi {

    @POST("/revoke")
    suspend fun revokeToken(
        @Body body: TokenRequestBody
    ): Response<Unit>

}