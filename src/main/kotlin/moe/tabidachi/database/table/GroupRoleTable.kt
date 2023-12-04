package moe.tabidachi.database.table

import org.jetbrains.exposed.dao.id.LongIdTable

object GroupRoleTable : LongIdTable("group_role") {
    val sid = reference("sid", SessionTable.id)
    val uid = reference("uid", UserTable.id)
    val type = enumeration<Type>("type")
    val canChangeGroupInfo = bool("can_change_group_info")
    val canDeleteMessage = bool("can_delete_message")
    val canBanUser = bool("can_ban_user")
    val canPinMessage = bool("can_pin_message")
    val canAddNewAdmin = bool("can_add_new_admin")

    enum class Type {
        OWNER, ADMIN
    }

    init {
        uniqueIndex(sid, uid)
    }
}