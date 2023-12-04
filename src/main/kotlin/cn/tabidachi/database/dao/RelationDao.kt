package cn.tabidachi.database.dao

import cn.tabidachi.database.model.Relation
import cn.tabidachi.database.table.RelationTable

interface RelationDao {
    fun findByPairUser(users: Pair<Long, Long>): Relation?
    fun saveByPairUser(users: Pair<Long, Long>, state: RelationTable.State, chatId: Long?): Relation
    fun updateByPairUser(users: Pair<Long, Long>, state: RelationTable.State?, chatId: Long?): Boolean
}