package cn.tabidachi

import cn.tabidachi.database.dao.DeviceDao
import cn.tabidachi.database.dao.SessionUserDao
import cn.tabidachi.database.dao.UserDao
import cn.tabidachi.database.model.Message
import cn.tabidachi.model.MessageType
import cn.tabidachi.model.WebSocketMessage
import cn.tabidachi.model.header
import cn.tabidachi.plugins.SignalingCommand
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object MessageManager {
    fun dispatch(message: WebSocketMessage) {

    }
}

data class Room(
    val user: Long,
) {
    val offer: MutableList<String> = mutableListOf()
    val answer: MutableList<String> = mutableListOf()
    val ice: MutableList<String> = mutableListOf()
}

class OnlineListener {
    fun register(set: Set<Long>) {

    }
}

@Serializable
data class OnlineStatus(
    val target: Long,
    val isOnline: Boolean
)

class WebSocketClient(
    private val deviceDao: DeviceDao, private val userDao: UserDao, private val sessionUserDao: SessionUserDao
) {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val mutex = Mutex()
    private val clients = mutableMapOf<Long, MutableList<WebSocketServerSession>>()

    // sid, uid list
//    private val rooms = mutableMapOf<Long, MutableList<Room>>()

    init {
        scope.launch {
            while (true) {
                println("当前在线人数：${clients.size}")
                clients.forEach {
                    println("uid ${it.key}, 设备数：${it.value.size}")
                }
                delay(10000)
            }
        }
        /*scope.launch {
            while (true) {
                println("当前房间数：${rooms.size}")
                rooms.forEach {
                    println("sid ${it.key}, 人数：${it.value.size}")
                }
                delay(10000)
            }
        }*/
    }

    // 被监听map，key 为被监听用户，value 为监听用户
    private val listener: MutableMap<Long, MutableSet<WebSocketServerSession>> = mutableMapOf()

    private fun listen(session: WebSocketServerSession, target: Long) {
        (listener[target] ?: mutableSetOf<WebSocketServerSession>().also {
            listener[target] = it
        }).apply { add(session) }
    }

    private fun unlisten(session: WebSocketServerSession, target: Long) {
        listener[target]?.remove(session)
    }

    private fun isOnline(target: Long): Boolean {
        return (clients[target]?.size ?: 0) > 0
    }

    private suspend fun sendOnlineStatus(session: WebSocketServerSession, onlineStatus: OnlineStatus) = runCatching {
        session.send(
            WebSocketMessage {
                header = header {
                    type = MessageType.OnlineStatus.Status.toString()
                }
                body = onlineStatus.let(Json::encodeToString).toByteArray()
            }.let(Json::encodeToString)
        )
    }

    fun online(uid: Long, session: WebSocketServerSession) {
        println("$uid online at $session")
        scope.launch {
            mutex.withLock {
                if (clients[uid] == null) {
                    clients[uid] = mutableListOf()
                }
                clients[uid]?.add(session)
                /*val sessionUsers = sessionUserDao.findByUserId(uid)
                sessionUsers.forEach { sessionUser ->
                    val exists = (rooms[sessionUser.sid] ?: rooms.put(sessionUser.sid, mutableListOf()))?.any { room ->
                        room.user == sessionUser.uid
                    }
                    if (exists != true) {
                        rooms[sessionUser.sid]?.add(Room(sessionUser.uid))
                    }
                }*/
            }
            val onlineStatus = OnlineStatus(uid, isOnline(uid))
            listener[uid]?.forEach { session ->
                sendOnlineStatus(session, onlineStatus).onFailure {
                    unlisten(session, uid)
                }
            }
        }
    }

    fun offline(uid: Long, session: WebSocketServerSession) {
        println("$uid offline at $session")
        scope.launch {
            mutex.withLock {
                val sessions = clients[uid]?.apply {
                    remove(session)
                    if (this.isEmpty()) {
                        clients.remove(uid)
                    }
                }
            }
            listener[uid]?.forEach { session ->
                val onlineStatus = OnlineStatus(uid, isOnline(uid))
                sendOnlineStatus(session, onlineStatus).onFailure {
                    unlisten(session, uid)
                }
            }
        }
    }

    fun onMessage(uid: Long, message: WebSocketMessage, session: WebSocketServerSession) = scope.launch {
        println(message)
        when (message.header.type) {
            MessageType.WebRTC.Any.toString() -> {
            }

            MessageType.WebRTC.Request.toString() -> {
                val target = String(message.body).toLong()
                println(target)
                val (_, username, _, avatar) = userDao.findById(uid) ?: return@launch
                deviceDao.findByUser(target).forEach { device ->
                    println(device.token)
                    kotlin.runCatching {
                        val message1 = ElectroFirebaseAdmin.buildMessage(
                            token = device.token, src = uid, dst = device.uid, image = avatar, username = username
                        )
                        ElectroFirebaseAdmin.sendMessage(message1)
                    }.onSuccess {
                        it.let(::println)
                    }.onFailure {
                        deviceDao.delete(device)
                        it.printStackTrace()
                    }
                }
            }

            MessageType.WebRTC.Response.toString() -> {
                val target = String(message.body).toLong()
                clients[target]?.forEach { session ->
                    kotlin.runCatching {
                        session.send(message.let(Json::encodeToString))
                    }.onFailure {
                        it.printStackTrace()
                    }
                }
            }

            MessageType.WebRTC.Offer.toString() -> {
                val (target, _) = String(message.body).let<String, WebRTCMessage>(Json::decodeFromString)
                clients[target]?.forEach { session ->
                    session.send(message.let(Json::encodeToString))
                }
            }

            MessageType.WebRTC.Answer.toString() -> {
                val (target, _) = String(message.body).let<String, WebRTCMessage>(Json::decodeFromString)
                clients[target]?.forEach { session ->
                    session.send(message.let(Json::encodeToString))
                }
            }

            MessageType.WebRTC.End.toString() -> {
                val target = String(message.body).toLong()
                clients[target]?.forEach { session ->
                    session.send(message.let(Json::encodeToString))
                }
            }

            MessageType.WebRTC.Ice.toString() -> {
                val (target, _) = String(message.body).let<String, WebRTCMessage>(Json::decodeFromString)
                clients[target]?.forEach { session ->
                    session.send(message.let(Json::encodeToString))
                }
            }

            MessageType.WebRTC.Command.toString() -> {
                val signalingMessage = String(message.body).let<String, WebRTCMessage>(Json::decodeFromString)
                println(signalingMessage)
                clients[signalingMessage.target]?.forEach { session ->
                    kotlin.runCatching {
                        session.send(message.let(Json::encodeToString))
                    }.onFailure {
                        it.printStackTrace()
                    }
                }
            }

            MessageType.OnlineStatus.Listen.toString() -> {
                val target = String(message.body).toLong()
                println("listen target: $target from $uid")
                listen(session, target)
                sendOnlineStatus(session, OnlineStatus(target, isOnline(target)))
            }

            MessageType.OnlineStatus.Unlisten.toString() -> {
                val target = String(message.body).toLong()
                println("unlisten target: $target from $uid")
                unlisten(session, target)
            }

            else -> {
                kotlin.runCatching {
                    Json.decodeFromString<Message>(String(message.body))
                }.onSuccess {

                }.onFailure {
                    println("不支持的消息类型：$message")
                    it.printStackTrace()
                }
            }
        }
    }

    fun notify(target: Long, type: MessageType, body: ByteArray) {
        clients[target]?.let { sessions ->
            kotlin.runCatching {
                val message = WebSocketMessage(
                    header = WebSocketMessage.Header(
                        type = type.toString(), timestamp = System.currentTimeMillis(), null
                    ), body = body
                )
                Json.encodeToString(message)
            }.onSuccess { message ->
                scope.launch {
                    sessions.forEach {
                        it.send(message)
                    }
                }
            }.onFailure {
                it.printStackTrace()
            }
        }
    }

    fun broadcast() {

    }
}

@Serializable
data class SignalingMessage(
    val target: Long, val command: SignalingCommand, val message: String
)

@Serializable
data class WebRTCMessage(
    val target: Long, val message: String
)