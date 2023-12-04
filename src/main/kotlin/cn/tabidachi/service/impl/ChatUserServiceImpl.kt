package cn.tabidachi.service.impl

import cn.tabidachi.database.dao.SessionUserDao
import cn.tabidachi.database.model.SessionUser
import cn.tabidachi.model.ResponseData
import cn.tabidachi.service.ChatUserService
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