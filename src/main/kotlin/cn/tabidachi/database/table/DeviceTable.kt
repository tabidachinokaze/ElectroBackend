package cn.tabidachi.database.table

import org.jetbrains.exposed.dao.id.LongIdTable

object DeviceTable : LongIdTable("device") {
    val uid = reference("uid", UserTable.id)
    val token = varchar("token", 255)

    init {
        uniqueIndex(uid, token)
    }
}