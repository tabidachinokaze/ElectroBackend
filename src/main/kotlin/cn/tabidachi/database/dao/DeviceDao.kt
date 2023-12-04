package cn.tabidachi.database.dao

import cn.tabidachi.database.model.Device

interface DeviceDao {
    fun findByUser(uid: Long): List<Device>
    fun delete(device: Device)
}