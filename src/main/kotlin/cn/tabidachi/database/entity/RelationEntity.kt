package cn.tabidachi.database.entity

import cn.tabidachi.database.table.RelationTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class RelationEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<RelationEntity>(RelationTable)

    var src by RelationTable.src
    var dst by RelationTable.dst
    var state by RelationTable.state
//    var sid by RelationTable.sid
}