package moe.tabidachi.database.model

import moe.tabidachi.database.entity.MessageEntity
import moe.tabidachi.database.table.MessageTable
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val mid: Long,
    val sid: Long,
    val uid: Long,
    val forward: Long?,
    val reply: Long?,
    val type: MessageTable.MessageType,
    val text: String?,
    val attachment: String?,
    val createTime: Long,
    val updateTime: Long
) {
    constructor(entity: MessageEntity) : this(
        entity.id.value,
        entity.sid.value,
        entity.uid.value,
        entity.forward?.value,
        entity.reply?.value,
        entity.type,
        entity.text,
        entity.attachment,
        entity.createTime.millis,
        entity.updateTime.millis,
    )
}