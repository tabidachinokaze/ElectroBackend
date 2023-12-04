package moe.tabidachi.database.model

import moe.tabidachi.database.entity.ChannelRoleEntity
import moe.tabidachi.database.table.ChannelRoleTable
import kotlinx.serialization.Serializable

@Serializable
data class ChannelRole(
    val sid: Long,
    val uid: Long,
    val type: ChannelRoleTable.Type,
    val canPostMessage: Boolean,
    val canBanUser: Boolean,
    val canEditMessageOfOthers: Boolean,
    val canDeleteMessageOfOthers: Boolean,
    val canAddNewAdmin: Boolean,
) {
    constructor(entity: ChannelRoleEntity) : this(
        sid = entity.sid.value,
        uid = entity.uid.value,
        type = entity.type,
        canPostMessage = entity.canPostMessage,
        canBanUser = entity.canBanUser,
        canEditMessageOfOthers = entity.canEditMessageOfOthers,
        canDeleteMessageOfOthers = entity.canDeleteMessageOfOthers,
        canAddNewAdmin = entity.canAddNewAdmin,
    )
}
