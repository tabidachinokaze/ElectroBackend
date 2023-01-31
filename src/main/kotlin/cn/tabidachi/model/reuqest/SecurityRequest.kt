package cn.tabidachi.model.reuqest

import kotlinx.serialization.Serializable

@Serializable
data class SecurityRequest(
    val email: String
)