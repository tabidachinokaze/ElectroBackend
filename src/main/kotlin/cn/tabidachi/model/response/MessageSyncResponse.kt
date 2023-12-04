package cn.tabidachi.model.response

import cn.tabidachi.database.model.Message
import kotlinx.serialization.Serializable

@Serializable
data class MessageSyncResponse(
    val updates: List<Message>,
    val deletes: List<Long>
)