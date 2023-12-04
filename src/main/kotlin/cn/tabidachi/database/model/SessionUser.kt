package cn.tabidachi.database.model

import cn.tabidachi.database.entity.SessionUserEntity
import cn.tabidachi.database.table.SessionUserTable
import kotlinx.serialization.Serializable

@Serializable
data class SessionUser(
    val sid: Long,
    val uid: Long,
    val lastReadTime: Long,
    val state: SessionUserTable.State,
    val extras: String?
) {
    constructor(entity: SessionUserEntity) : this(
        entity.sid.value,
        entity.uid.value,
        entity.lastReadTime.millis,
        entity.state,
        entity.extras
    )
}