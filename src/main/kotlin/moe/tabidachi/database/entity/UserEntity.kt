package moe.tabidachi.database.entity

import moe.tabidachi.database.table.UserTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class UserEntity(userId: EntityID<Long>) : LongEntity(userId) {
    companion object : LongEntityClass<UserEntity>(UserTable)

    var username by UserTable.username
    var password by UserTable.password
    var email by UserTable.email
    var avatar by UserTable.avatar
}