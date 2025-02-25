package moe.tabidachi.database.dao.impl

import moe.tabidachi.database.dao.RelationDao
import moe.tabidachi.database.entity.SessionEntity
import moe.tabidachi.database.entity.RelationEntity
import moe.tabidachi.database.entity.UserEntity
import moe.tabidachi.database.model.Relation
import moe.tabidachi.database.table.RelationTable
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class RelationDaoImpl : RelationDao {
    override fun findByPairUser(users: Pair<Long, Long>): Relation? = transaction {
        RelationEntity.find {
            (RelationTable.src eq users.first) and (RelationTable.dst eq users.second)
        }.map(::Relation).singleOrNull()
    }

    override fun saveByPairUser(users: Pair<Long, Long>, state: RelationTable.State, chatId: Long?): Relation =
        transaction {
            RelationEntity.new {
                UserEntity.findById(users.first)?.let {
                    src = it.id
                }
                UserEntity.findById(users.second)?.let {
                    dst = it.id
                }
                this.state = state
//                chatId?.let(SessionEntity::findById)?.let {
//                    this.sid = it.id
//                }
            }.let(::Relation)
        }

    override fun updateByPairUser(users: Pair<Long, Long>, state: RelationTable.State?, chatId: Long?): Boolean = transaction {
        RelationTable.update(
            where = {
                (RelationTable.src eq users.first) and (RelationTable.dst eq users.second)
            }
        ) {
            state?.let(it, RelationTable.state)
//            chatId?.let(SessionEntity::findById)?.id.let(it, RelationTable.sid)
        } > 0
    }
}