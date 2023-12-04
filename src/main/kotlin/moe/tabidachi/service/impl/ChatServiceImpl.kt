package moe.tabidachi.service.impl

import moe.tabidachi.WebSocketClient
import moe.tabidachi.database.dao.ChatDao
import moe.tabidachi.database.dao.SessionUserDao
import moe.tabidachi.database.dao.MessageDao
import moe.tabidachi.database.dao.UserDao
import moe.tabidachi.database.model.Session
import moe.tabidachi.database.model.Message
import moe.tabidachi.database.table.SessionTable
import moe.tabidachi.database.table.SessionUserTable
import moe.tabidachi.model.ResponseData
import moe.tabidachi.model.response.ChatResponse
import moe.tabidachi.service.ChatService
import io.ktor.http.*
import kotlinx.serialization.Serializable

class ChatServiceImpl(
    private val chatDao: ChatDao,
    private val sessionUserDao: SessionUserDao,
    private val messageDao: MessageDao,
    private val userDao: UserDao,
    private val ws: WebSocketClient
) : ChatService {
    override fun getAllChat(userId: Long): ResponseData<List<Session>> {
        val chatUsers = sessionUserDao.findByUserId(userId)
        val chats = chatUsers.mapNotNull {
            chatDao.findByChatId(it.sid)
        }
        return ResponseData(HttpStatusCode.OK, "获取成功", chats)
    }

    override fun getChatByChatId(chatId: Long): ResponseData<Session?> {
        val chat = chatDao.findByChatId(chatId) ?: return ResponseData(HttpStatusCode.NotFound, "查询失败", null)
        return ResponseData(HttpStatusCode.OK, "查询成功", chat)
    }

    override fun directMessage(userId: Long, recipientId: Long, message: Message): ResponseData<Unit> {
        val newMessage = when (message.sid) {
            null -> {
                val chatIdList = sessionUserDao.findByUserId(userId).map {
                    it.sid
                } intersect sessionUserDao.findByUserId(recipientId).map {
                    it.sid
                }.toSet()
                println("")
                val chat = chatIdList.mapNotNull {
                    chatDao.findByChatIdAndType(it, SessionTable.SessionType.P2P)
                }.map {
                    println("私聊${it.sid}")
                    it
                }.singleOrNull() ?: chatDao.saveChat(SessionTable.SessionType.P2P).also {
                    sessionUserDao.saveChatUser(it.sid, userId, SessionUserTable.State.REQUEST)
                    sessionUserDao.saveChatUser(it.sid, recipientId, SessionUserTable.State.NONE)
                }
                message.copy(sid = chat.sid)
            }

            else -> {
                message
            }
        }
        kotlin.runCatching {
//            messageDao.saveMessage(newMessage)
        }.getOrElse {
            it.printStackTrace()
            return ResponseData(HttpStatusCode.InternalServerError, it.message.toString(), Unit)
        }
        val chatNotification = ChatNotification(newMessage.sid, recipientId, ChatNotification.Type.NEW)
//        ws.onMessage(chatNotification)
        return ResponseData(HttpStatusCode.OK, "发送成功", Unit)
    }

    override fun getChatByPairUser(
        users: Pair<Long, Long>, createIfNotExists: Boolean
    ): Triple<HttpStatusCode, String, ChatResponse?> {
        val chatResponse = sessionUserDao.findPairUserChatId(users).mapNotNull {
            chatDao.findByChatId(it)
        }.singleOrNull { it.type == SessionTable.SessionType.P2P }.let {
            it ?: if (createIfNotExists) {
                chatDao.saveChat(SessionTable.SessionType.P2P).also {
                    sessionUserDao.saveChatUser(it.sid, users.first, SessionUserTable.State.REQUEST)
                    sessionUserDao.saveChatUser(it.sid, users.second, SessionUserTable.State.NONE)
                }
            } else {
                null
            }
        }?.let { chat ->
            val chatUser = sessionUserDao.findByChatIdAndUserId(chat.sid, users.first)
            val count = chatUser?.let { messageDao.getMessageByChatIdAfterTime(chat.sid, it.lastReadTime).size }
            val targetUser = userDao.findById(users.second)
            val latestMessage = messageDao.getLatestMessage(chat.sid)
            ChatResponse(
                chat.sid,
                chat.type,
                targetUser?.avatar,
                targetUser?.username,
                latestMessage?.text,
                latestMessage?.createTime,
                count,
                users.second.toString()
            )
        } ?: return Triple(HttpStatusCode.NotFound, "未找到", null)
        return Triple(HttpStatusCode.OK, "success", chatResponse)
    }

    override fun getChatIdByPairUser(users: Pair<Long, Long>): Triple<HttpStatusCode, String, Long?> {
        val chatId = sessionUserDao.findPairUserChatId(users).mapNotNull {
            chatDao.findByChatId(it)
        }.singleOrNull { it.type == SessionTable.SessionType.P2P }?.sid ?: return Triple(HttpStatusCode.NotFound, "未找到", null)
        return Triple(HttpStatusCode.OK, "查找成功", chatId)
    }
}


@Serializable
data class ChatNotification(
    val chatId: Long,
    val recipientId: Long,
    val type: Type,
) {
    enum class Type {
        NEW, DELETE
    }
}
