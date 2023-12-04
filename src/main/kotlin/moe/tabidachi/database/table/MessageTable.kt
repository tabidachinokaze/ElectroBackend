package moe.tabidachi.database.table

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.jodatime.datetime

object MessageTable : LongIdTable(name = "message") {
    val sid = reference("sid", SessionTable.id)
    val uid = reference("uid", UserTable.id)
    val forward = reference("forward", MessageTable.id).nullable()
    val reply = reference("reply", MessageTable.id).nullable()
    val type = enumeration<MessageType>("type")
    val text = text("text").nullable()
    val attachment = mediumText("attachment").nullable()
    val createTime = datetime("create_time")
    val updateTime = datetime("update_time")

    enum class MessageType {
        TEXT, IMAGE, AUDIO, VIDEO, LOCATION, VOICE, FILE, WEBRTC
    }
}