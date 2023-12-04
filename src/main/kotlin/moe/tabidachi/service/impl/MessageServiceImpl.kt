package moe.tabidachi.service.impl

import moe.tabidachi.database.dao.MessageDao
import moe.tabidachi.database.model.Message
import moe.tabidachi.model.MessageRequest
import moe.tabidachi.model.MessageSendResponse
import moe.tabidachi.model.ResponseData
import moe.tabidachi.model.reuqest.MessageSendRequest
import moe.tabidachi.service.MessageService
import io.ktor.http.*
import kotlin.properties.Delegates

class MessageServiceImpl(
    private val messageDao: MessageDao
) : MessageService {
    override fun getMessageByChatIdAfterTime(chatId: Long, after: Long): ResponseData<List<Message>> {
        val messages = messageDao.getMessageByChatIdAfterTime(chatId, after)
        return ResponseData(HttpStatusCode.OK, "查询成功", messages)
    }

    override fun getMessageByChatId(chatId: Long): ResponseData<List<Message>> {
        val messages = messageDao.getMessage(chatId)
        return ResponseData(HttpStatusCode.OK, "查询成功", messages)
    }

    override fun getMessage(userId: Long, messageRequest: MessageRequest): ResponseData<List<Message>> {
        val messages = when (messageRequest.type) {
            MessageRequest.Type.NONE -> {
                messageDao.getMessage(messageRequest.sid)
            }

            MessageRequest.Type.UNREAD -> {
                messageDao.getUnreadMessage(messageRequest.sid, userId)
            }

            MessageRequest.Type.BETWEEN -> {
                messageDao.getMessageBetweenTime(messageRequest.sid, messageRequest.between, messageRequest.limit)
            }
        }
        return ResponseData(HttpStatusCode.OK, "查询成功", messages)
    }

    override fun getMessage(mid: Long): ResponseData<Message?> {
        return ResponseData(HttpStatusCode.OK, "查询成功", messageDao.getMessageById(mid))
    }

    override fun saveMessage(messageSendRequest: MessageSendRequest): ResponseData<MessageSendResponse> {
        val message = messageDao.saveMessage(messageSendRequest)
        val messageResponse = MessageSendResponse(
            messageSendRequest.id,
            message.mid,
            message.sid,
            message.uid,
            message.forward,
            message.reply,
            message.type,
            message.text,
            message.attachment,
            message.createTime,
            message.updateTime
        )
        return ResponseData(HttpStatusCode.OK, "发送成功", messageResponse)
    }
}