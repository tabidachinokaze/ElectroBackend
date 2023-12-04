package cn.tabidachi.database.dao

import cn.tabidachi.database.model.Message
import cn.tabidachi.model.reuqest.MessageSendRequest

interface MessageDao {
    fun saveMessage(message: MessageSendRequest): Message
    fun getMessageByChatIdAfterTime(chatId: Long, after: Long): List<Message>
    fun getLatestMessage(chatId: Long): Message?
    fun getUnreadMessage(chatId: Long, userId: Long): List<Message>
    fun getMessageBetweenTime(chatId: Long, limit: Pair<Long?, Long?>, size: Int): List<Message>
    fun getMessage(chatId: Long): List<Message>
    fun getMessageById(mid: Long): Message?
}