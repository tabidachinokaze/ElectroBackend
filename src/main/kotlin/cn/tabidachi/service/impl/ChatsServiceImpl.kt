package cn.tabidachi.service.impl

import cn.tabidachi.database.dao.ChatDao
import cn.tabidachi.database.dao.SessionUserDao
import cn.tabidachi.database.dao.MessageDao
import cn.tabidachi.database.dao.UserDao
import cn.tabidachi.database.table.SessionTable
import cn.tabidachi.database.table.SessionUserTable
import cn.tabidachi.model.ResponseData
import cn.tabidachi.model.response.ChatResponse
import cn.tabidachi.service.ChatsService
import io.ktor.http.*

class ChatsServiceImpl(
    private val sessionUserDao: SessionUserDao,
    private val chatDao: ChatDao,
    private val userDao: UserDao,
    private val messageDao: MessageDao
) : ChatsService {
    override fun getChatListByUserId(userId: Long): ResponseData<List<ChatResponse>> {
        // TODO 获取该用户的所有 chatId
        val chatIdList = sessionUserDao.findByUserId(userId)
        // TODO 通过chatId查询chat列表，要过滤被封禁的chat，过滤不存在的chat
        val chats = chatIdList.mapNotNull {
            val chat = chatDao.findByChatId(it.sid)
            if (chat == null) {
                null
            } else {
                chat to it.lastReadTime
            }
        }
        // TODO 转成用户视图
        val responses = chats.map { (chat, lastReadTime) ->
            // TODO 获取chat中的最新一条message
            val latestMessage = messageDao.getLatestMessage(chat.sid)
            // TODO 获取chat中未读消息数
            val unreadMessageCount = messageDao.getMessageByChatIdAfterTime(chat.sid, lastReadTime).size
            when (chat.type) {
                SessionTable.SessionType.P2P -> {
                    // TODO 如果是私聊，则获取对方的信息
                    val user = sessionUserDao.findByChatId(chat.sid).singleOrNull { it.uid != userId }?.let {
                        userDao.findById(it.uid)
                    }
                    ChatResponse(
                        chat.sid,
                        chat.type,
                        user?.avatar,
                        user?.username,
                        latestMessage?.text,
                        latestMessage?.createTime,
                        unreadMessageCount,
                        user?.uid?.toString(),
                    )
                }

                else -> {
                    ChatResponse(
                        chat.sid,
                        chat.type,
                        chat.image,
                        chat.title,
                        latestMessage?.text,
                        latestMessage?.createTime,
                        unreadMessageCount,
                        null,
                    )
                }
            }
        }
        return ResponseData(HttpStatusCode.OK, "获取成功", responses)
    }

    override fun getChatByUserId(chatId: Long, userId: Long): ResponseData<ChatResponse?> {
        val chatResponse = sessionUserDao.findByChatIdAndUserId(chatId, userId)?.let { chatUser ->
            chatDao.findByChatId(chatUser.sid)?.let { chat ->
                val latestMessage = messageDao.getLatestMessage(chat.sid)
                val unreadMessageCount = messageDao.getMessageByChatIdAfterTime(chat.sid, chatUser.lastReadTime).size
                when (chat.type) {
                    SessionTable.SessionType.P2P -> {
                        // TODO 如果是私聊，则获取对方的信息
                        val user = sessionUserDao.findByChatId(chat.sid).singleOrNull { it.uid != userId }?.let {
                            userDao.findById(it.uid)
                        }
                        ChatResponse(
                            chat.sid,
                            chat.type,
                            user?.avatar,
                            user?.username,
                            latestMessage?.text,
                            latestMessage?.createTime,
                            unreadMessageCount,
                            user?.uid?.toString(),
                        )
                    }

                    else -> {
                        ChatResponse(
                            chat.sid,
                            chat.type,
                            chat.image,
                            chat.title,
                            latestMessage?.text,
                            latestMessage?.createTime,
                            unreadMessageCount,
                            null,
                        )
                    }
                }
            }
        }
        return if (chatResponse == null) {
            ResponseData(HttpStatusCode.NotFound, "获取失败", null)
        } else {
            ResponseData(HttpStatusCode.OK, "获取成功", chatResponse)
        }
    }

    override fun getDirectChatByUsers(users: Pair<Long, Long>): ResponseData<ChatResponse?> {
        val chatIdList = sessionUserDao.findPairUserChatId(users)
        return chatIdList.mapNotNull {
            chatDao.findByChatId(it)
        }.singleOrNull { it.type == SessionTable.SessionType.P2P }?.let { chat ->
            getChatByUserId(chat.sid, users.first)
        } ?: ResponseData(HttpStatusCode.NotFound, "查找失败", null)
    }

    override fun getOrCreateDirectChatByUsers(users: Pair<Long, Long>): ResponseData<ChatResponse?> {
        val chatIdList = sessionUserDao.findPairUserChatId(users)
        return chatIdList.mapNotNull {
            chatDao.findByChatId(it)
        }.singleOrNull { it.type == SessionTable.SessionType.P2P }.let {
            val chatId = if (it == null) {
                val chat = chatDao.saveChat(SessionTable.SessionType.P2P)
                sessionUserDao.saveChatUser(chat.sid, users.first, SessionUserTable.State.REQUEST)
                sessionUserDao.saveChatUser(chat.sid, users.second, SessionUserTable.State.NONE)
                chat.sid
            } else it.sid
            getChatByUserId(chatId, users.first)
        }
    }
}