package moe.tabidachi.model.reuqest

import kotlinx.serialization.Serializable

@Serializable
data class ChannelUpdateRequest(
    val image: String?,
    val title: String?,
    val description: String?
)