package moe.tabidachi.database.model

import moe.tabidachi.database.entity.GroupRoleEntity
import moe.tabidachi.database.table.GroupRoleTable
import kotlinx.serialization.Serializable

@Serializable
data class GroupRole(
    val sid: Long,
    val uid: Long,
    val type: GroupRoleTable.Type,
    val canChangeGroupInfo: Boolean,
    val canDeleteMessage: Boolean,
    val canBanUser: Boolean,
    val canPinMessage: Boolean,
    val canAddNewAdmin: Boolean,
) {
    constructor(entity: GroupRoleEntity) : this(
        sid = entity.sid.value,
        uid = entity.uid.value,
        type = entity.type,
        canChangeGroupInfo = entity.canChangeGroupInfo,
        canDeleteMessage = entity.canDeleteMessage,
        canBanUser = entity.canBanUser,
        canPinMessage = entity.canPinMessage,
        canAddNewAdmin = entity.canAddNewAdmin,
    )
}