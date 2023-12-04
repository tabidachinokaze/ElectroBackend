package cn.tabidachi.model.reuqest

import kotlinx.serialization.Serializable

@Serializable
data class MessageSyncRequest(
    val mid: Long,
    val updateTime: Long,
)