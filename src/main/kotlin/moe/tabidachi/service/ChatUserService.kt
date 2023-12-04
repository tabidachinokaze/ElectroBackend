package moe.tabidachi.service

import moe.tabidachi.database.model.SessionUser
import moe.tabidachi.model.ResponseData

interface ChatUserService {
    fun getChatUserListByChatId(chatId: Long): ResponseData<List<SessionUser>>
    fun joinChatRequest(userId: Long, chatId: Long)
}