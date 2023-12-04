package cn.tabidachi.database.entity

import cn.tabidachi.database.table.DeviceTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID


class DeviceEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<DeviceEntity>(DeviceTable)

    var uid by DeviceTable.uid
    var token by DeviceTable.token
}