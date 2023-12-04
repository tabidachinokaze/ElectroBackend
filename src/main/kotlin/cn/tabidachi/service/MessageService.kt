package cn.tabidachi.service

import cn.tabidachi.database.model.Message
import cn.tabidachi.model.MessageRequest
import cn.tabidachi.model.MessageSendResponse
import cn.tabidachi.model.ResponseData
import cn.tabidachi.model.reuqest.MessageSendRequest

interface MessageService {
    fun getMessageByChatIdAfterTime(chatId: Long, after: Long): ResponseData<List<Message>>
    fun getMessageByChatId(chatId: Long): ResponseData<List<Message>>
    fun getMessage(userId: Long, messageRequest: MessageRequest): ResponseData<List<Message>>
    fun getMessage(mid: Long): ResponseData<Message?>
    fun saveMessage(messageSendRequest: MessageSendRequest): ResponseData<MessageSendResponse>
}