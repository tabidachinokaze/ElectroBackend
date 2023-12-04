package moe.tabidachi.service

import moe.tabidachi.database.model.Message
import moe.tabidachi.model.MessageRequest
import moe.tabidachi.model.MessageSendResponse
import moe.tabidachi.model.ResponseData
import moe.tabidachi.model.reuqest.MessageSendRequest

interface MessageService {
    fun getMessageByChatIdAfterTime(chatId: Long, after: Long): ResponseData<List<Message>>
    fun getMessageByChatId(chatId: Long): ResponseData<List<Message>>
    fun getMessage(userId: Long, messageRequest: MessageRequest): ResponseData<List<Message>>
    fun getMessage(mid: Long): ResponseData<Message?>
    fun saveMessage(messageSendRequest: MessageSendRequest): ResponseData<MessageSendResponse>
}