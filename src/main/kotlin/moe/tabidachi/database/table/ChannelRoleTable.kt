package moe.tabidachi.database.table

import org.jetbrains.exposed.dao.id.LongIdTable

object ChannelRoleTable : LongIdTable("channel_role") {
    val sid = reference("sid", SessionTable.id)
    val uid = reference("uid", UserTable.id)
    val type = enumeration<Type>("type")
    val canPostMessage = bool("can_post_message")
    val canBanUser = bool("can_ban_user")
    val canEditMessageOfOthers = bool("can_edit_message_of_others")
    val canDeleteMessageOfOthers = bool("can_delete_message_of_others")
    val canAddNewAdmin = bool("can_add_new_admin")

    enum class Type {
        OWNER, ADMIN
    }

    init {
        uniqueIndex(sid, uid)
    }
}