package moe.tabidachi.database.model

import moe.tabidachi.database.entity.RelationEntity
import moe.tabidachi.database.table.RelationTable
import kotlinx.serialization.Serializable

@Serializable
data class Relation(
    val src: Long,
    val dst: Long,
    val state: RelationTable.State,
//    val sid: Long?
) {
    constructor(entity: RelationEntity) : this(
        entity.src.value,
        entity.dst.value,
        entity.state,
//        entity.sid?.value
    )
}