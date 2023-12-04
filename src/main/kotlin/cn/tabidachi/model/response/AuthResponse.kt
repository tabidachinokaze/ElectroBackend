package cn.tabidachi.model.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val uid: Long,
    val token: String,
)