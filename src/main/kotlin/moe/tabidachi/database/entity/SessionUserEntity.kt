package moe.tabidachi.database.entity

import moe.tabidachi.database.table.SessionUserTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class SessionUserEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<SessionUserEntity>(SessionUserTable)

    var sid by SessionUserTable.sid
    var uid by SessionUserTable.uid
    var lastReadTime by SessionUserTable.lastReadTime
    var state by SessionUserTable.state
    // extras 被谁邀请
    var extras by SessionUserTable.extras
}