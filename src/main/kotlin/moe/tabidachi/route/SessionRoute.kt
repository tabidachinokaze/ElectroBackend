package moe.tabidachi.route

import moe.tabidachi.WebSocketClient
import moe.tabidachi.database.entity.*
import moe.tabidachi.database.model.Session
import moe.tabidachi.database.model.User
import moe.tabidachi.database.table.*
import moe.tabidachi.exception.BadRequestException
import moe.tabidachi.ext.uid
import moe.tabidachi.model.MessageType
import moe.tabidachi.model.response.DialogResponse
import moe.tabidachi.model.response.Response
import moe.tabidachi.model.reuqest.SessionCreateRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.koin.ktor.ext.inject

fun Route.session() {
    val ws: WebSocketClient by inject()
    /**
     * 获取用户的所有会话
     * @response [Sessions]
     */
    get("/sessions") {
        transaction {
            SessionUserEntity.find {
                SessionUserTable.uid eq uid
            }.map {
                it.sid.value
            }
        }.let {
            call.respond(HttpStatusCode.OK, Response(HttpStatusCode.OK.value, "查询成功", it))
        }
    }
    get("/dialogs") {
        val uid = uid
        transaction {
            SessionUserEntity.find { SessionUserTable.uid eq uid }.mapNotNull {
                sessionUserToDialog(it)
            }
        }.let {
            val status = HttpStatusCode.OK
            call.respond(status, Response(status.value, "查找成功", it))
        }
    }
    get("/dialogs/{sid}") {
        val sid = call.parameters["sid"]?.toLong() ?: throw BadRequestException("参数错误")
        transaction {
            SessionUserEntity.find { (SessionUserTable.sid eq sid) and (SessionUserTable.uid eq uid) }.mapNotNull {
                sessionUserToDialog(it)
            }.singleOrNull()
        }.let {
            val status = HttpStatusCode.OK
            call.respond(status, Response(status.value, "查找成功", it))
        }
    }
    /**
     * 获取session的信息，以及用户与session相关的信息
     * @response
     */
    get("/sessions/{sid}") {
        val sid = call.parameters["sid"]?.toLong() ?: throw BadRequestException("参数错误")
        transaction {
            SessionEntity.findById(sid)?.let(::Session)
        }.let {
            val status = HttpStatusCode.OK
            call.respond(status, Response(status.value, "ok", it))
        }
    }
    /**
     * 获取会话的所有消息
     */
    get("/sessions/{sid}/messages") {

    }
    /**
     * 获取用户在会话中的未读消息数
     */
    get("/sessions/{sid}/messages/unread-count") {

    }
    /**
     * 获取最新一条消息
     */
    get("/sessions/{sid}/messages/last") {

    }
    /**
     * 获取指定消息
     */
    get("/sessions/{sid}/messages/{mid}") {

    }
    /**
     * 获取与某个用户的的会话id
     * @param target 目标用户
     * @response [Int] 会话id
     */
    get("/sessions/p2p/{target}") {
        val target = call.parameters["target"]?.toLong() ?: throw BadRequestException("参数错误")
        findP2pSessionId(uid to target).let {
            call.respond(HttpStatusCode.OK, Response(HttpStatusCode.OK.value, "查询成功", it))
        }
    }
    /**
     * 创建与某用户的 p2p 会话
     * @param target 目标用户
     * @response [Int] 会话id
     */
    post("/sessions/p2p/{target}") {
        val target = call.parameters["target"]?.toLong() ?: throw BadRequestException("参数错误")
        val users = uid to target
        (findP2pSessionId(users) ?: createP2pSessionId(users)).let {
            call.respond(HttpStatusCode.OK, Response(HttpStatusCode.OK.value, "创建成功", it))
            transaction {
                SessionUserEntity.find {
                    SessionUserTable.sid eq it
                }.forEach {
                    ws.notify(it.uid.value, MessageType.Dialog.New, it.sid.toString().toByteArray())
                }
            }
        }
    }
    /**
     * 获取room
     */
    get("/sessions/room/{sid}") {

    }
    /**
     * 创建一个群组
     */
    post("/sessions/room") {

    }
    post("/session") {
        val (type, title, description, image) = call.receive<SessionCreateRequest>()
        val uid = uid
        transaction {
            SessionEntity.new {
                this.type = type
                this.title = title
                this.description = description
                this.image = image
                val time = DateTime.now()
                this.createTime = time
                this.updateTime = time
                this.isPublic = true
                this.needRequest = true
            }.also {
                when (type) {
                    SessionTable.SessionType.ROOM -> {
                        GroupRoleEntity.new {
                            SessionEntity.findById(it.id)?.let {
                                this.sid = it.id
                            }
                            UserEntity.findById(uid)?.let {
                                this.uid = it.id
                            }
                            this.type = GroupRoleTable.Type.OWNER
                            canChangeGroupInfo = true
                            canDeleteMessage = true
                            canBanUser = true
                            canPinMessage = true
                            canAddNewAdmin = true
                        }
                    }
                    SessionTable.SessionType.CHANNEL -> {
                        ChannelRoleEntity.new {
                            SessionEntity.findById(it.id)?.let {
                                this.sid = it.id
                            }
                            UserEntity.findById(uid)?.let {
                                this.uid = it.id
                            }
                            this.type = ChannelRoleTable.Type.OWNER
                            canPostMessage = true
                            canBanUser = true
                            canEditMessageOfOthers = true
                            canDeleteMessageOfOthers = true
                            canAddNewAdmin = true
                        }
                    }
                    else -> {

                    }
                }
                SessionUserEntity.new {
                    this.sid = it.id
                    UserEntity.findById(uid)?.let {
                        this.uid = it.id
                    }
                    this.lastReadTime = DateTime.now()
                    this.state = SessionUserTable.State.CREATOR
                }
            }
        }.let {
            val status = HttpStatusCode.OK
            call.respond(status, Response(status.value, "创建成功", it.id.value))
        }
    }
    get("/sessions/{sid}/users") {
        val sid = call.parameters["sid"]?.toLong() ?: throw BadRequestException("参数错误")
        transaction {
            SessionEntity.findById(sid)!!.type to
            SessionUserEntity.find {
                SessionUserTable.sid eq sid
            }.map {
                it.uid.value
            }
        }.let {
            val status = HttpStatusCode.OK
            call.respond(status, Response(status.value, "查询成功", it))
        }
    }
    post("/session/invite") {
        val (sid, target) = kotlin.runCatching {
            call.receive<InviteRequest>()
        }.getOrElse {
            throw it
        }
        val uid = uid
        transaction {
            SessionUserEntity.new {
                SessionEntity.findById(sid)?.let {
                    this.sid = it.id
                }
                UserEntity.findById(target)?.let {
                    this.uid = it.id
                }
                this.lastReadTime = DateTime.now()
                this.state = SessionUserTable.State.INVITED
                this.extras = uid.toString()
            }
        }.let {
            val status = HttpStatusCode.OK
            call.respond(status,Response(status.value, "", true))
        }
    }

    post("/session/{sid}/request") {
        val sid = call.parameters["sid"]?.toLong() ?: throw BadRequestException("参数错误")
        val uid = uid
        transaction {
            SessionUserEntity.new {
                SessionEntity.findById(sid)?.let {
                    this.sid = it.id
                    if (it.needRequest) {
                        this.state = SessionUserTable.State.REQUEST
                    } else {
                        this.state = SessionUserTable.State.MEMBER
                    }
                }
                UserEntity.findById(uid)?.let {
                    this.uid = it.id
                }
                this.lastReadTime = DateTime.now()
            }
        }.let {
            val status = HttpStatusCode.OK
            call.respond(status,Response(status.value, "", it.state))
        }
    }

    post("/session/{sid}/exit") {
        val sid = call.parameters["sid"]?.toLong() ?: throw BadRequestException("参数错误")
        val uid = uid
        transaction {
            SessionUserEntity.find {
                (SessionUserTable.sid eq sid) and (SessionUserTable.uid eq uid)
            }.singleOrNull()?.let {
                exec("SET FOREIGN_KEY_CHECKS=0;")
                if (it.state == SessionUserTable.State.CREATOR) {
                    MessageEntity.find {
                        MessageTable.sid eq sid
                    }.forEach { it.delete() }
                    SessionUserTable.deleteWhere {
                        (SessionUserTable.sid eq sid)
                    }
                    SessionEntity.findById(sid)?.delete()
                } else {
                    it.delete()
                }
                exec("SET FOREIGN_KEY_CHECKS=1;")
            } != null
        }.let {
            val status = HttpStatusCode.OK
            call.respond(status, Response(status.value, "ok", sid))
        }
    }

    get("/session/{sid}/admin") {
        val sid = call.parameters["sid"]?.toLong() ?: throw BadRequestException("参数错误")
        transaction {
            SessionUserEntity.find {
                (SessionUserTable.sid eq sid) and (SessionUserTable.state eq SessionUserTable.State.CREATOR)
            }.mapNotNull {
                UserEntity.findById(it.uid)
            }.map(::User)
        }.let {
            val status = HttpStatusCode.OK
            call.respond(status, Response(status.value, "ok", it))
        }
    }
}

@Serializable
data class InviteRequest(
    val sid: Long,
    val target: Long,
)

fun sessionUserToDialog(entity: SessionUserEntity): DialogResponse? {
    val messageEntity =
        MessageEntity.find { MessageTable.sid eq entity.sid }.orderBy(MessageTable.createTime to SortOrder.DESC)
            .limit(1).singleOrNull()
    val text = messageEntity?.text ?: ""
    val subtitle = when (messageEntity?.type) {
        MessageTable.MessageType.IMAGE -> "[图片]${text}"
        MessageTable.MessageType.AUDIO -> "[音频]${text}"
        MessageTable.MessageType.VIDEO -> "[视频]${text}"
        MessageTable.MessageType.LOCATION -> "[位置]${text}"
        MessageTable.MessageType.VOICE -> "[语音]${text}"
        MessageTable.MessageType.FILE -> "[文件]${text}"
        MessageTable.MessageType.WEBRTC -> "[通话]${text}"
        else -> text
    }
    val messageCount =
        MessageEntity.find { (MessageTable.sid eq entity.sid) and (MessageTable.createTime greater entity.lastReadTime) }.count()
    return SessionEntity.findById(entity.sid)?.let {
        var extras: String? = null
        var image: String? = it.image
        var title: String? = it.title
        if (it.type == SessionTable.SessionType.P2P) {
            val target =
                SessionUserEntity.find { (SessionUserTable.sid eq it.id) and (SessionUserTable.uid neq entity.uid) }
                    .singleOrNull()?.uid?.value
            target?.let { it1 ->
                UserEntity.findById(it1).let {
                    image = it?.avatar
                    title = it?.username
                }
            }
            extras = target.toString()
        }
        DialogResponse(
            it.id.value,
            entity.uid.value,
            it.type,
            image,
            title,
            subtitle,
            messageEntity?.createTime?.millis,
            messageCount.toInt(),
            extras
        )
    }
}

fun findP2pSessionId(users: Pair<Long, Long>): Long? = transaction {
    SessionUserEntity.find { SessionUserTable.uid eq users.first }.asSequence().map {
        SessionUserEntity.find { (SessionUserTable.sid eq it.sid) and (SessionUserTable.uid eq users.second) }
    }.flatten().mapNotNull {
        SessionEntity.findById(it.sid)
    }.filter {
        it.type == SessionTable.SessionType.P2P
    }.map {
        it.id.value
    }.toList()
}.singleOrNull()

fun createP2pSessionId(users: Pair<Long, Long>): Long = transaction {
    val now = DateTime.now()
    SessionEntity.new {
        this.type = SessionTable.SessionType.P2P
        this.createTime = now
        this.updateTime = now
        this.isPublic = false
        this.needRequest = true
    }.also { session ->
        SessionUserEntity.new {
            this.sid = session.id
            UserEntity.findById(users.first)?.let {
                this.uid = it.id
            }
            this.lastReadTime = now
            this.state = SessionUserTable.State.CREATOR
        }
        SessionUserEntity.new {
            this.sid = session.id
            UserEntity.findById(users.second)?.let {
                this.uid = it.id
            }
            this.lastReadTime = now
            this.state = SessionUserTable.State.INVITED
        }
    }.id.value
}


class RoomCreateRequest(
    val title: String, val description: String?, val image: String?
)

class SessionDao {
    fun findSessionsByUid(uid: Long) = transaction {

    }
}
