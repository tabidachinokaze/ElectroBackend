package cn.tabidachi.model.reuqest

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val email: String,
    val password: String,
    val code: String
)