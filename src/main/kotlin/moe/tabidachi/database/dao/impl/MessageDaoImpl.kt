package moe.tabidachi.database.dao.impl

import moe.tabidachi.database.dao.MessageDao
import moe.tabidachi.database.entity.SessionEntity
import moe.tabidachi.database.entity.SessionUserEntity
import moe.tabidachi.database.entity.MessageEntity
import moe.tabidachi.database.entity.UserEntity
import moe.tabidachi.database.model.Message
import moe.tabidachi.database.table.SessionUserTable
import moe.tabidachi.database.table.MessageTable
import moe.tabidachi.model.reuqest.MessageSendRequest
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime

class MessageDaoImpl : MessageDao {
    override fun saveMessage(message: MessageSendRequest): Message = transaction {
        MessageEntity.new {
            message.sid.let(SessionEntity::findById)?.let {
                this.sid = it.id
            }
            UserEntity.findById(message.uid)?.let {
                this.uid = it.id
            }
            message.forward?.let(MessageEntity::findById)?.let {
                this.forward = it.id
            }
            message.reply?.let(MessageEntity::findById)?.let {
                this.reply = it.id
            }
            this.type = message.type
            this.text = message.text
            this.attachment = message.attachment
            val time = DateTime.now()
            this.createTime = time
            this.updateTime = time
        }.let(::Message)
    }

    override fun getMessageByChatIdAfterTime(chatId: Long, after: Long): List<Message> = transaction {
        if (after == 0L) {
            MessageEntity.find {
                MessageTable.sid eq chatId
            }.map(::Message)
        } else {
            MessageEntity.find {
                (MessageTable.sid eq chatId) and (MessageTable.createTime.greater(DateTime(after)))
            }.map(::Message)
        }
    }

    override fun getLatestMessage(chatId: Long): Message? = transaction {
        MessageEntity.find {
            (MessageTable.sid eq chatId)
        }.orderBy(MessageTable.createTime to SortOrder.DESC).limit(1).singleOrNull()?.let(::Message)
    }

    override fun getUnreadMessage(chatId: Long, userId: Long): List<Message> = transaction {
        SessionUserEntity.find {
            (SessionUserTable.sid eq chatId) and (SessionUserTable.uid eq userId)
        }.singleOrNull()?.let {
            MessageEntity.find {
                MessageTable.createTime greater it.lastReadTime
            }
        }?.map(::Message) ?: emptyList()
    }

    override fun getMessageBetweenTime(chatId: Long, limit: Pair<Long?, Long?>, size: Int): List<Message> = transaction {
        val after: Op<Boolean> = if (limit.first == null) Op.TRUE else MessageTable.createTime greater DateTime(limit.first)
        val before: Op<Boolean> = if (limit.second == null) Op.TRUE else MessageTable.createTime less DateTime(limit.second)
        MessageEntity.find {
            (MessageTable.sid eq chatId) and after and before
        }.orderBy(MessageTable.createTime to SortOrder.DESC).let {
            if (size > 0) {
                it.limit(size)
            } else it
        }.map(::Message)
    }

    override fun getMessage(chatId: Long): List<Message> = transaction {
        MessageEntity.find {
            MessageTable.sid eq chatId
        }.map(::Message)
    }

    override fun getMessageById(mid: Long): Message? = transaction {
        MessageEntity.find {
            MessageTable.id eq mid
        }.singleOrNull()?.let(::Message)
    }
}