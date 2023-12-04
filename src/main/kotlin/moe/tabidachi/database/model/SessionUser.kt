package moe.tabidachi.database.model

import moe.tabidachi.database.entity.SessionUserEntity
import moe.tabidachi.database.table.SessionUserTable
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