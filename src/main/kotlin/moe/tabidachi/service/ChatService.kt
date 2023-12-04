package moe.tabidachi.service

import moe.tabidachi.database.model.Session
import moe.tabidachi.database.model.Message
import moe.tabidachi.model.ResponseData
import moe.tabidachi.model.response.ChatResponse
import io.ktor.http.*

interface ChatService {
    fun getAllChat(userId: Long): ResponseData<List<Session>>
    fun getChatByChatId(chatId: Long): ResponseData<Session?>
    fun directMessage(userId: Long, recipientId: Long, message: Message): ResponseData<Unit>
    fun getChatByPairUser(users: Pair<Long, Long>, createIfNotExists: Boolean): Triple<HttpStatusCode, String, ChatResponse?>
    fun getChatIdByPairUser(users: Pair<Long, Long>): Triple<HttpStatusCode, String, Long?>
}