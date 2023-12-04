package cn.tabidachi.database.dao

import cn.tabidachi.database.model.SessionUser
import cn.tabidachi.database.table.SessionUserTable

interface SessionUserDao {
    fun saveChatUser(
        chatId: Long,
        userId: Long,
        state: SessionUserTable.State
    ): SessionUser

    fun removeChatUserByUid(userId: Long): Boolean
    fun removeChatUserBySid(chatId: Long): Boolean
    fun removeChatUser(chatId: Long, userId: Long): Boolean
    fun findByChatId(chatId: Long): List<SessionUser>
    fun findByUserId(userId: Long): List<SessionUser>
    fun findByChatIdAndUserId(chatId: Long, userId: Long): SessionUser?
    fun findPairUserChatId(users: Pair<Long, Long>): List<Long>
}