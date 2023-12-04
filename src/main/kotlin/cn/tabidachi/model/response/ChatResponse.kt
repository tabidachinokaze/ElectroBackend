package cn.tabidachi.model.response

import cn.tabidachi.database.table.SessionTable
import kotlinx.serialization.Serializable

@Serializable
data class ChatResponse(
    val chatId: Long,
    val type: SessionTable.SessionType,
    val image: String?,
    val title: String?,
    val subtitle: String?,
    val latest: Long?,
    val unread: Int?,
    val extend: String?
)