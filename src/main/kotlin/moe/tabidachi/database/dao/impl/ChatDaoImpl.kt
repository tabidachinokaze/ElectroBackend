package moe.tabidachi.database.dao.impl

import moe.tabidachi.database.dao.ChatDao
import moe.tabidachi.database.entity.SessionEntity
import moe.tabidachi.database.model.Session
import moe.tabidachi.database.table.SessionTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime

class ChatDaoImpl : ChatDao {
    override fun saveChat(
        type: SessionTable.SessionType,
        title: String?,
        description: String?,
        image: String?,
        isPublic: Boolean,
        needRequest: Boolean
    ): Session = transaction {
        SessionEntity.new {
            this.type = type
            this.title = title
            this.description = description
            this.image = image
            this.createTime = DateTime.now()
            this.updateTime = DateTime.now()
            this.isPublic = isPublic
            this.needRequest = needRequest
        }.let(::Session)
    }

    override fun removeChat(sid: Long): Boolean = transaction {
        SessionTable.deleteWhere { SessionTable.id eq sid } > 0
    }

    override fun updateChat(
        sid: Long,
        title: String?,
        description: String?,
        image: String?,
        isPublic: Boolean?,
        needRequest: Boolean?
    ): Boolean = transaction {
        SessionTable.update({
            SessionTable.id eq sid
        }) {
            title?.let(it, SessionTable.title)
            description?.let(it, SessionTable.description)
            image?.let(it, SessionTable.image)
            it[updateTime] = DateTime.now()
            isPublic?.let(it, SessionTable.isPublic)
            needRequest?.let(it, SessionTable.needRequest)
        } > 0
    }

    override fun findByChatId(sid: Long): Session? = transaction {
        SessionEntity.find { SessionTable.id eq sid }.singleOrNull()?.let(::Session)
    }

    override fun findByChatIdAndType(sid: Long, type: SessionTable.SessionType): Session? = transaction {
        SessionEntity.find { (SessionTable.id eq sid) and (SessionTable.type eq type) }.singleOrNull()?.let(::Session)
    }

    private fun <T> T.let(statement: UpdateStatement, column: Column<T>) {
        statement[column] = this
    }
}