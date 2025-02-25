package moe.tabidachi.model.response

import moe.tabidachi.database.table.SessionTable
import kotlinx.serialization.Serializable

@Serializable
data class SessionSearchResponse(
    val sid: Long,
    val type: SessionTable.SessionType,
    val title: String?,
    val description: String?,
    val image: String?,
    val createTime: Long,
    val count: Int
)