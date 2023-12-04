package cn.tabidachi.database.entity

import cn.tabidachi.database.table.SessionTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class SessionEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<SessionEntity>(SessionTable)

    var type by SessionTable.type
    var title by SessionTable.title
    var description by SessionTable.description
    var image by SessionTable.image
    var createTime by SessionTable.createTime
    var updateTime by SessionTable.updateTime
    var isPublic by SessionTable.isPublic
    var needRequest by SessionTable.needRequest
}