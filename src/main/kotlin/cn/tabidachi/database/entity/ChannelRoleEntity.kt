package cn.tabidachi.database.entity

import cn.tabidachi.database.table.ChannelRoleTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ChannelRoleEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ChannelRoleEntity>(ChannelRoleTable)

    var sid by ChannelRoleTable.sid
    var uid by ChannelRoleTable.uid
    var type by ChannelRoleTable.type
    var canPostMessage by ChannelRoleTable.canPostMessage
    var canBanUser by ChannelRoleTable.canBanUser
    var canEditMessageOfOthers by ChannelRoleTable.canEditMessageOfOthers
    var canDeleteMessageOfOthers by ChannelRoleTable.canDeleteMessageOfOthers
    var canAddNewAdmin by ChannelRoleTable.canAddNewAdmin
}