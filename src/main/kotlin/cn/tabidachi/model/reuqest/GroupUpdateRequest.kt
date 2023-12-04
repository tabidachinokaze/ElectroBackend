package cn.tabidachi.model.reuqest

import kotlinx.serialization.Serializable

@Serializable
data class GroupUpdateRequest(
    val image: String?,
    val title: String?,
    val description: String?
)