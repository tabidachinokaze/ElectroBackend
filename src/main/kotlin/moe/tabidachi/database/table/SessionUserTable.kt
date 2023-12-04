package moe.tabidachi.database.table

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.jodatime.datetime

object SessionUserTable : LongIdTable("session_user") {
    val sid = reference("sid", SessionTable.id)
    val uid = reference("uid", UserTable.id)
    val lastReadTime = datetime("last_read_time")
    val state = enumeration<State>("state")
    val extras = text("extras").nullable()

    init {
        uniqueIndex(sid, uid)
    }

    enum class State {
        NONE,
        // 创建者
        CREATOR,
        // 请求加入
        REQUEST,
        // 会话成员
        MEMBER,
        // 被封禁
        BANNED,
        // 被邀请
        INVITED
    }
}