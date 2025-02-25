package moe.tabidachi.model.response

import moe.tabidachi.database.model.Message
import kotlinx.serialization.Serializable

@Serializable
data class MessageSyncResponse(
    val updates: List<Message>,
    val deletes: List<Long>
)