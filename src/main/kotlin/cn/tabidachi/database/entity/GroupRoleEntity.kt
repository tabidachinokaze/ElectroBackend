package cn.tabidachi.database.entity

import cn.tabidachi.database.table.GroupRoleTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class GroupRoleEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<GroupRoleEntity>(GroupRoleTable)

    var sid by GroupRoleTable.sid
    var uid by GroupRoleTable.uid
    var type by GroupRoleTable.type
    var canChangeGroupInfo by GroupRoleTable.canChangeGroupInfo
    var canDeleteMessage by GroupRoleTable.canDeleteMessage
    var canBanUser by GroupRoleTable.canBanUser
    var canPinMessage by GroupRoleTable.canPinMessage
    var canAddNewAdmin by GroupRoleTable.canAddNewAdmin
}