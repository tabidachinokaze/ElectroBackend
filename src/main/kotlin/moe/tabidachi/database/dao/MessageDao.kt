package moe.tabidachi.database.dao

import moe.tabidachi.database.model.Message
import moe.tabidachi.model.reuqest.MessageSendRequest

interface MessageDao {
    fun saveMessage(message: MessageSendRequest): Message
    fun getMessageByChatIdAfterTime(chatId: Long, after: Long): List<Message>
    fun getLatestMessage(chatId: Long): Message?
    fun getUnreadMessage(chatId: Long, userId: Long): List<Message>
    fun getMessageBetweenTime(chatId: Long, limit: Pair<Long?, Long?>, size: Int): List<Message>
    fun getMessage(chatId: Long): List<Message>
    fun getMessageById(mid: Long): Message?
}