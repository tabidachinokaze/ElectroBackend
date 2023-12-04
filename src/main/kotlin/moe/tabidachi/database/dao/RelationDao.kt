package moe.tabidachi.database.dao

import moe.tabidachi.database.model.Relation
import moe.tabidachi.database.table.RelationTable

interface RelationDao {
    fun findByPairUser(users: Pair<Long, Long>): Relation?
    fun saveByPairUser(users: Pair<Long, Long>, state: RelationTable.State, chatId: Long?): Relation
    fun updateByPairUser(users: Pair<Long, Long>, state: RelationTable.State?, chatId: Long?): Boolean
}