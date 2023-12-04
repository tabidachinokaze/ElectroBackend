package cn.tabidachi.database.table

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.jodatime.datetime

/**
 * 会话表，消息的载体，会话可是是一个私聊、群组，频道等等，必须由用户创建。
 */
object SessionTable : LongIdTable(name = "session") {
    val type = enumeration<SessionType>("type")
    val title = varchar("title", 32).nullable()
    val description = varchar("description", 256).nullable()
    val image = text("image").nullable()
    val createTime = datetime("create_time")
    val updateTime = datetime("update_time")
    val isPublic = bool("is_public")
    val needRequest = bool("need_request")

    enum class SessionType {
        NONE, P2P, ROOM, CHANNEL
    }
}