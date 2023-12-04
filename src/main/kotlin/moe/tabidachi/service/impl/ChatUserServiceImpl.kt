package moe.tabidachi.service.impl

import moe.tabidachi.database.dao.SessionUserDao
import moe.tabidachi.database.model.SessionUser
import moe.tabidachi.model.ResponseData
import moe.tabidachi.service.ChatUserService
import io.ktor.http.*

class ChatUserServiceImpl(
    private val sessionUserDao: SessionUserDao
) : ChatUserService {
    override fun getChatUserListByChatId(chatId: Long): ResponseData<List<SessionUser>> {
        val chatUsers = sessionUserDao.findByChatId(chatId)
        return ResponseData(HttpStatusCode.OK, "查询成功", chatUsers)
    }

    override fun joinChatRequest(userId: Long, chatId: Long) {

    }
}