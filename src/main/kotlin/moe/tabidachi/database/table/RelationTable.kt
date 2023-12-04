package moe.tabidachi.database.table

import org.jetbrains.exposed.dao.id.LongIdTable

/**
 * 会话关系表，
 */
object RelationTable : LongIdTable("relation") {
    val src = reference("src", UserTable.id)
    val dst = reference("dst", UserTable.id)
    val state = enumeration<State>("relation")
//    val sid = reference("sid", SessionTable.id).nullable()

    enum class State {
        NONE, CONTACT, BLOCK
    }

    init {
        uniqueIndex(src, dst)
        check {
            src neq dst
        }
    }
}