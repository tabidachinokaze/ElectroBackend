package moe.tabidachi.database.entity

import moe.tabidachi.database.table.MessageTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class MessageEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<MessageEntity>(MessageTable)

    var sid by MessageTable.sid
    var uid by MessageTable.uid
    var forward by MessageTable.forward
    var reply by MessageTable.reply
    var type by MessageTable.type
    var text by MessageTable.text
    var attachment by MessageTable.attachment
    var createTime by MessageTable.createTime
    var updateTime by MessageTable.updateTime
}