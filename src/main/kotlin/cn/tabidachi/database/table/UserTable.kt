package cn.tabidachi.database.table

import org.jetbrains.exposed.dao.id.LongIdTable

object UserTable : LongIdTable(name = "user", columnName = "user_id") {
    val username = varchar("username", length = 16)
    val password = varchar("password", length = 64)
    val email = varchar("email", length = 254).uniqueIndex()
}