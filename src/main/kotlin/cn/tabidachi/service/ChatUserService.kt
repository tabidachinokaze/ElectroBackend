package cn.tabidachi.service

import cn.tabidachi.database.model.SessionUser
import cn.tabidachi.model.ResponseData

interface ChatUserService {
    fun getChatUserListByChatId(chatId: Long): ResponseData<List<SessionUser>>
    fun joinChatRequest(userId: Long, chatId: Long)
}