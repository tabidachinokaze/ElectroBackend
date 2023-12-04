package moe.tabidachi.model.reuqest

import kotlinx.serialization.Serializable

@Serializable
data class SecurityRequest(
    val email: String
)