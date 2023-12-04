package moe.tabidachi.database.dao

import moe.tabidachi.database.model.Device

interface DeviceDao {
    fun findByUser(uid: Long): List<Device>
    fun delete(device: Device)
}