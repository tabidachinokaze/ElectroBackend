package moe.tabidachi.database.dao.impl

import moe.tabidachi.database.dao.DeviceDao
import moe.tabidachi.database.entity.DeviceEntity
import moe.tabidachi.database.model.Device
import moe.tabidachi.database.table.DeviceTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

class DeviceDaoImpl : DeviceDao {
    override fun findByUser(uid: Long): List<Device> = transaction {
        DeviceEntity.find {
            DeviceTable.uid eq uid
        }.map(::Device)
    }

    override fun delete(device: Device): Unit = transaction {
        DeviceTable.deleteWhere {
            (uid eq device.uid) and (token eq device.token)
        }
    }
}