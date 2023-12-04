package cn.tabidachi.model.response

import cn.tabidachi.database.table.SessionTable
import kotlinx.serialization.Serializable

@Serializable
class DialogResponse(
    val sid: Long,
    val uid: Long,
    val type: SessionTable.SessionType,
    val image: String?,
    val title: String?,
    val subtitle: String?,
    val latest: Long?,
    val unread: Int?,
    val extras: String?
)