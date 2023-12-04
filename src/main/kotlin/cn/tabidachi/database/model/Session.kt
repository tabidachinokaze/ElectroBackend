package cn.tabidachi.database.model

import cn.tabidachi.database.entity.SessionEntity
import cn.tabidachi.database.table.SessionTable
import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val sid: Long,
    val type: SessionTable.SessionType,
    val title: String?,
    val description: String?,
    val image: String?,
    val createTime: Long,
    val updateTime: Long,
    val isPublic: Boolean,
    val needRequest: Boolean
) {
    constructor(entity: SessionEntity) : this(
        entity.id.value,
        entity.type,
        entity.title,
        entity.description,
        entity.image,
        entity.createTime.millis,
        entity.updateTime.millis,
        entity.isPublic,
        entity.needRequest
    )
}
