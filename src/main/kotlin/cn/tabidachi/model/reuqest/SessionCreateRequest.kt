package cn.tabidachi.model.reuqest

import cn.tabidachi.database.table.SessionTable
import kotlinx.serialization.Serializable

@Serializable
data class SessionCreateRequest(
    val type: SessionTable.SessionType,
    val title: String?,
    val description: String?,
    val image: String?,
)

