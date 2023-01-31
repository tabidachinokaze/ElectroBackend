package cn.tabidachi.model.response

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    override val code: Int,
    override val message: String,
    val token: String? = null
) : Response