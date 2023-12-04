package cn.tabidachi.service

import cn.tabidachi.database.model.Session
import cn.tabidachi.database.model.Message
import cn.tabidachi.model.ResponseData
import cn.tabidachi.model.response.ChatResponse
import io.ktor.http.*

interface ChatService {
    fun getAllChat(userId: Long): ResponseData<List<Session>>
    fun getChatByChatId(chatId: Long): ResponseData<Session?>
    fun directMessage(userId: Long, recipientId: Long, message: Message): ResponseData<Unit>
    fun getChatByPairUser(users: Pair<Long, Long>, createIfNotExists: Boolean): Triple<HttpStatusCode, String, ChatResponse?>
    fun getChatIdByPairUser(users: Pair<Long, Long>): Triple<HttpStatusCode, String, Long?>
}