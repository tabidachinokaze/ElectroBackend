package cn.tabidachi.service

import cn.tabidachi.model.ResponseData
import cn.tabidachi.model.response.ChatResponse

interface ChatsService {
    fun getChatListByUserId(userId: Long): ResponseData<List<ChatResponse>>
    fun getChatByUserId(chatId: Long, userId: Long): ResponseData<ChatResponse?>
    fun getDirectChatByUsers(users: Pair<Long, Long>): ResponseData<ChatResponse?>
    fun getOrCreateDirectChatByUsers(users: Pair<Long, Long>): ResponseData<ChatResponse?>
}