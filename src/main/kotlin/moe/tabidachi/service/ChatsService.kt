package moe.tabidachi.service

import moe.tabidachi.model.ResponseData
import moe.tabidachi.model.response.ChatResponse

interface ChatsService {
    fun getChatListByUserId(userId: Long): ResponseData<List<ChatResponse>>
    fun getChatByUserId(chatId: Long, userId: Long): ResponseData<ChatResponse?>
    fun getDirectChatByUsers(users: Pair<Long, Long>): ResponseData<ChatResponse?>
    fun getOrCreateDirectChatByUsers(users: Pair<Long, Long>): ResponseData<ChatResponse?>
}