package moe.tabidachi.database.model

import moe.tabidachi.database.entity.DeviceEntity

data class Device(
    val uid: Long,
    val token: String
) {
    constructor(entity: DeviceEntity) : this(
        entity.uid.value,
        entity.token
    )
}