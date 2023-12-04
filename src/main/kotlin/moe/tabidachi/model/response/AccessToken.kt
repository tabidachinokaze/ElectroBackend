package moe.tabidachi.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccessToken(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_in")
    val expiresIn: Int,
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("scope")
    val scope: String?,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("refresh_token")
    val refreshToken: String
)