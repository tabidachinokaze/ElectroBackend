package moe.tabidachi.model

import moe.tabidachi.database.table.MessageTable
import kotlinx.serialization.Serializable

@Serializable
data class MessageSendResponse(
    val id: String,
    val mid: Long,
    val sid: Long,
    val uid: Long,
    val forward: Long?,
    val reply: Long?,
    val type: MessageTable.MessageType,
    val text: String?,
    val attachment: String?,
    val createTime: Long,
    val updateTime: Long
)