package moe.tabidachi.route

import moe.tabidachi.WebSocketClient
import moe.tabidachi.database.entity.MessageEntity
import moe.tabidachi.database.entity.RelationEntity
import moe.tabidachi.database.entity.SessionEntity
import moe.tabidachi.database.entity.SessionUserEntity
import moe.tabidachi.database.model.Message
import moe.tabidachi.database.table.RelationTable
import moe.tabidachi.database.table.SessionTable
import moe.tabidachi.database.table.SessionUserTable
import moe.tabidachi.exception.BadRequestException
import moe.tabidachi.ext.uid
import moe.tabidachi.model.MessageRequest
import moe.tabidachi.model.MessageType
import moe.tabidachi.model.response.MessageSyncResponse
import moe.tabidachi.model.response.Response
import moe.tabidachi.model.response.emptyData
import moe.tabidachi.model.reuqest.MessageSendRequest
import moe.tabidachi.model.reuqest.MessageSyncRequest
import moe.tabidachi.service.MessageService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.joda.time.DateTime
import org.koin.ktor.ext.inject

fun Route.message() {
    val messageService: MessageService by inject()
    val ws: WebSocketClient by inject()
    route("/messages") {
        post {
            val messageRequest = kotlin.runCatching {
                call.receive<MessageRequest>()
            }.getOrElse {
                throw BadRequestException(it.message)
            }
            val (status, message, data) = messageService.getMessage(uid, messageRequest)
            call.respond(status, Response(status.value, message, data))
        }
        get("/{mid}") {
            val mid = call.parameters["mid"]?.toLong() ?: throw BadRequestException("参数错误")
            val (status, message, data) = messageService.getMessage(mid)
            call.respond(status, Response(status.value, message, data))
        }
        post("/sync") {
            val requests = kotlin.runCatching {
                call.receive<List<MessageSyncRequest>>()
            }.getOrElse {
                throw BadRequestException(it.message)
            }
            val updates = mutableListOf<Message>()
            val deletes = mutableListOf<Long>()
            transaction {
                requests.forEach { request ->
                    MessageEntity.findById(request.mid)?.let { entity ->
                        if (request.updateTime != entity.updateTime.millis) {
                            updates.add(Message(entity))
                        }
                    } ?: deletes.add(request.mid)
                }
            }
            val status = HttpStatusCode.OK
            call.respond(status, Response(status.value, "ok", MessageSyncResponse(updates, deletes)))
        }
        post("/{sid}/read/{time}") {
            val sid = call.parameters["sid"]?.toLong() ?: throw BadRequestException("参数错误")
            val time = call.parameters["time"]?.toLong() ?: throw BadRequestException("参数错误")
            val uid = uid
            transaction {
                SessionUserTable.update(
                    where = {
                        (SessionUserTable.sid eq sid) and (SessionUserTable.uid eq uid)
                    }
                ) {
                    it[lastReadTime] = DateTime(time)
                } > 0
            }.let {
                val status = HttpStatusCode.OK
                call.respond(status, Response(status.value, "ok", time))
            }
        }
    }
    route("/message") {
        post {
            val uid = uid
            val messageSendRequest = kotlin.runCatching {
                call.receive<MessageSendRequest>()
            }.getOrElse {
                throw BadRequestException("参数错误")
            }
            transaction {
                SessionEntity.findById(messageSendRequest.sid)?.let { sessionEntity ->
                    if (sessionEntity.type == SessionTable.SessionType.P2P) {
                        val target = SessionUserEntity.find {
                            (SessionUserTable.sid eq sessionEntity.id)
                        }.single { it.uid.value != uid }
                        RelationEntity.find {
                            (RelationTable.src eq target.uid) and (RelationTable.dst eq uid)
                        }.single().let {
                            it.state != RelationTable.State.BLOCK
                        }
                    } else {
                        true
                    }
                } ?: false
            }.let {
                if (it) {
                    val (status, message, data) = messageService.saveMessage(messageSendRequest)
                    call.respond(status, Response(status.value, message, data))
                    if (status == HttpStatusCode.OK) transaction {
                        SessionUserEntity.find {
                            SessionUserTable.sid eq data.sid
                        }.forEach {
                            ws.notify(
                                it.uid.value,
                                MessageType.Message.New,
                                Pair(data.sid, data.mid).let(Json::encodeToString).toByteArray()
                            )
                        }
                    }
                } else {
                    val status = HttpStatusCode.Forbidden
                    call.respond(status, Response(status.value, "Forbidden", emptyData<Long>()))
                }
            }
        }
        delete("/{mid}") {
            val mid = call.parameters["mid"]?.toLong() ?: throw BadRequestException("参数错误")
            transaction {
                exec("SET FOREIGN_KEY_CHECKS=0;")
                MessageEntity.findById(mid)?.let { message ->
                    message.delete()
                    SessionUserEntity.find {
                        SessionUserTable.sid eq message.sid
                    }.forEach {
                        ws.notify(
                            it.uid.value,
                            MessageType.Message.Delete,
                            Pair(message.sid.value, message.id.value).let(Json::encodeToString).toByteArray()
                        )
                    }
                }
                exec("SET FOREIGN_KEY_CHECKS=1;")
            }.let {
                val status = HttpStatusCode.OK
                call.respond(status, Response(status.value, "ok", mid))
            }
        }
    }
}