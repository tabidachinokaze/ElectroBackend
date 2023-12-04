package cn.tabidachi.database.dao.impl

import cn.tabidachi.database.dao.SessionUserDao
import cn.tabidachi.database.entity.SessionEntity
import cn.tabidachi.database.entity.SessionUserEntity
import cn.tabidachi.database.entity.UserEntity
import cn.tabidachi.database.model.SessionUser
import cn.tabidachi.database.table.SessionTable
import cn.tabidachi.database.table.SessionUserTable
import cn.tabidachi.database.table.UserTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

class SessionUserDaoImpl : SessionUserDao {
    override fun saveChatUser(chatId: Long, userId: Long, state: SessionUserTable.State): SessionUser = transaction {
        SessionUserEntity.new {
            this.sid = SessionEntity.find { SessionTable.id eq chatId }.single().id
            this.uid = UserEntity.find { UserTable.id eq userId }.single().id
            this.lastReadTime = DateTime.now()
            this.state = state
        }.let(::SessionUser)
    }

    override fun removeChatUserByUid(userId: Long): Boolean = transaction {
        SessionUserTable.deleteWhere { SessionUserTable.uid eq userId } > 0
    }

    override fun removeChatUserBySid(chatId: Long): Boolean = transaction {
        SessionUserTable.deleteWhere { SessionUserTable.sid eq chatId } > 0
    }

    override fun removeChatUser(chatId: Long, userId: Long): Boolean = transaction {
        SessionUserTable.deleteWhere {
            (SessionUserTable.uid eq userId) and (SessionUserTable.sid eq chatId)
        } > 0
    }

    override fun findByChatId(chatId: Long): List<SessionUser> = transaction {
        SessionUserEntity.find { SessionUserTable.sid eq chatId }.map(::SessionUser)
    }

    override fun findByUserId(userId: Long): List<SessionUser> = transaction {
        SessionUserEntity.find { SessionUserTable.uid eq userId }.map(::SessionUser)
    }

    override fun findByChatIdAndUserId(chatId: Long, userId: Long): SessionUser? = transaction {
        SessionUserEntity.find { (SessionUserTable.uid eq userId) and (SessionUserTable.sid eq chatId) }
            .singleOrNull()
            ?.let(::SessionUser)
    }

    override fun findPairUserChatId(users: Pair<Long, Long>): List<Long> = transaction {
        SessionUserEntity.find {
            SessionUserTable.uid eq users.first
        }.map {
            SessionUserEntity.find {
                (SessionUserTable.uid eq users.second) and (SessionUserTable.sid eq it.sid)
            }
        }.flatten().map {
            it.sid.value
        }
    }
}














