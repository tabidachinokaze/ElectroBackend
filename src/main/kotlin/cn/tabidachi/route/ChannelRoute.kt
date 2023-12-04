package cn.tabidachi.route

import cn.tabidachi.database.entity.ChannelRoleEntity
import cn.tabidachi.database.entity.SessionEntity
import cn.tabidachi.database.entity.SessionUserEntity
import cn.tabidachi.database.entity.UserEntity
import cn.tabidachi.database.model.ChannelRole
import cn.tabidachi.database.table.ChannelRoleTable
import cn.tabidachi.database.table.SessionTable
import cn.tabidachi.database.table.SessionUserTable
import cn.tabidachi.exception.BadRequestException
import cn.tabidachi.ext.uid
import cn.tabidachi.model.response.Response
import cn.tabidachi.model.response.emptyData
import cn.tabidachi.model.reuqest.ChannelUpdateRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

fun Route.channel() {
    route("/channel/{sid}") {
        patch {
            val sid = call.parameters["sid"]?.toLong() ?: throw BadRequestException("参数错误")
            uid
            val (image, title, description) = kotlin.runCatching {
                call.receive<ChannelUpdateRequest>()
            }.getOrElse {
                throw BadRequestException(it.message)
            }
            if ((image ?: title ?: description) == null) {
                val status = HttpStatusCode.BadRequest
                call.respond(status, Response(status.value, "字段不能全为空", emptyData<Long>()))
            }
            transaction {
                SessionTable.update(where = {
                    SessionTable.id eq sid
                }) {
                    image?.let(it, SessionTable.image)
                    title?.let(it, SessionTable.title)
                    description?.let(it, SessionTable.description)
                }
            }.let {
                val status = HttpStatusCode.OK
                call.respond(status, Response(status.value, "ok", sid))
            }
        }
        route("/admins") {
            get {
                val sid = call.parameters["sid"]?.toLong() ?: throw BadRequestException("参数错误")
                transaction {
                    ChannelRoleEntity.find {
                        (ChannelRoleTable.sid eq sid)
                    }.map(::ChannelRole)
                }.let {
                    val status = HttpStatusCode.OK
                    call.respond(status, Response(status.value, "ok", it))
                }
            }
        }
        route("/admin/{target}") {
            delete {
                val sid = call.parameters["sid"]?.toLong() ?: throw BadRequestException("参数错误")
                val target = call.parameters["target"]?.toLong() ?: throw BadRequestException("参数错误")
                uid
                transaction {
                    ChannelRoleEntity.find {
                        (ChannelRoleTable.sid eq sid) and (ChannelRoleTable.uid eq target)
                    }.single().let {
                        if (it.type != ChannelRoleTable.Type.OWNER) {
                            it.delete()
                            true
                        } else false
                    }
                }.let {
                    if (it) {
                        val status = HttpStatusCode.OK
                        call.respond(status, Response(status.value, "ok", target))
                    } else {
                        val status = HttpStatusCode.Forbidden
                        call.respond(status, Response(status.value, "Forbidden", target))
                    }
                }
            }
            post {
                val sid = call.parameters["sid"]?.toLong() ?: throw BadRequestException("参数错误")
                val target = call.parameters["target"]?.toLong() ?: throw BadRequestException("参数错误")
                uid
                transaction {
                    ChannelRoleEntity.new {
                        SessionEntity.findById(sid)?.let {
                            this.sid = it.id
                        }
                        UserEntity.findById(target)?.let {
                            this.uid = it.id
                        }
                        this.type = ChannelRoleTable.Type.ADMIN
                        canPostMessage = false
                        canBanUser = false
                        canEditMessageOfOthers = false
                        canDeleteMessageOfOthers = false
                        canAddNewAdmin = false
                    }
                }.let(::ChannelRole).let {
                    val status = HttpStatusCode.OK
                    call.respond(status, Response(status.value, "ok", it))
                }
            }
            get {
                val sid = call.parameters["sid"]?.toLong() ?: throw BadRequestException("参数错误")
                val target = call.parameters["target"]?.toLong() ?: throw BadRequestException("参数错误")
                uid
                transaction {
                    ChannelRoleEntity.find {
                        (ChannelRoleTable.sid eq sid) and (ChannelRoleTable.uid eq target)
                    }.single().let(::ChannelRole)
                }.let {
                    val status = HttpStatusCode.OK
                    call.respond(status, Response(status.value, "ok", it))
                }
            }
        }
        route("/member/{target}") {
            delete {
                val sid = call.parameters["sid"]?.toLong() ?: throw BadRequestException("参数错误")
                val target = call.parameters["target"]?.toLong() ?: throw BadRequestException("参数错误")
                uid
                transaction {
                    SessionUserEntity.find {
                        (SessionUserTable.sid eq sid) and (SessionUserTable.uid eq target)
                    }.single().let {
                        if (it.state != SessionUserTable.State.CREATOR) {
                            it.delete()
                            true
                        } else false
                    }
                }.let {
                    if (it) {
                        val status = HttpStatusCode.OK
                        call.respond(status, Response(status.value, "ok", target))
                    } else {
                        val status = HttpStatusCode.Forbidden
                        call.respond(status, Response(status.value, "Forbidden", target))
                    }
                }
            }
        }
    }
}

private fun <T> T.let(statement: UpdateStatement, column: Column<T>) {
    statement[column] = this
}