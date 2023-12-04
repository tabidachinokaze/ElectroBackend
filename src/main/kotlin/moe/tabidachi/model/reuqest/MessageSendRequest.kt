package moe.tabidachi.model.reuqest

import moe.tabidachi.database.table.MessageTable
import kotlinx.serialization.Serializable

@Serializable
data class MessageSendRequest(
    val id: String,
    val sid: Long,
    val uid: Long,
    val forward: Long?,
    val reply: Long?,
    val type: MessageTable.MessageType,
    val text: String?,
    val attachment: String?,
    val createTime: Long
)