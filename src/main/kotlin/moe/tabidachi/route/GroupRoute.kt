package moe.tabidachi.route

import moe.tabidachi.database.entity.GroupRoleEntity
import moe.tabidachi.database.entity.SessionEntity
import moe.tabidachi.database.entity.SessionUserEntity
import moe.tabidachi.database.entity.UserEntity
import moe.tabidachi.database.model.GroupRole
import moe.tabidachi.database.table.GroupRoleTable
import moe.tabidachi.database.table.SessionTable
import moe.tabidachi.database.table.SessionUserTable
import moe.tabidachi.exception.BadRequestException
import moe.tabidachi.ext.uid
import moe.tabidachi.model.response.Response
import moe.tabidachi.model.response.emptyData
import moe.tabidachi.model.reuqest.GroupUpdateRequest
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

fun Route.group() {
    route("/group/{sid}") {
        patch {
            val sid = call.parameters["sid"]?.toLong() ?: throw BadRequestException("参数错误")
            uid
            val (image, title, description) = kotlin.runCatching {
                call.receive<GroupUpdateRequest>()
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
                    GroupRoleEntity.find {
                        (GroupRoleTable.sid eq sid)
                    }.map(::GroupRole)
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
                    GroupRoleEntity.find {
                        (GroupRoleTable.sid eq sid) and (GroupRoleTable.uid eq target)
                    }.single().let {
                        if (it.type != GroupRoleTable.Type.OWNER) {
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
                    GroupRoleEntity.new {
                        SessionEntity.findById(sid)?.let {
                            this.sid = it.id
                        }
                        UserEntity.findById(target)?.let {
                            this.uid = it.id
                        }
                        type = GroupRoleTable.Type.ADMIN
                        canChangeGroupInfo = false
                        canDeleteMessage = false
                        canBanUser = false
                        canPinMessage = false
                        canAddNewAdmin = false
                    }
                }.let(::GroupRole).let {
                    val status = HttpStatusCode.OK
                    call.respond(status, Response(status.value, "ok", it))
                }
            }
            get {
                val sid = call.parameters["sid"]?.toLong() ?: throw BadRequestException("参数错误")
                val target = call.parameters["target"]?.toLong() ?: throw BadRequestException("参数错误")
                uid
                transaction {
                    GroupRoleEntity.find {
                        (GroupRoleTable.sid eq sid) and (GroupRoleTable.uid eq target)
                    }.single().let(::GroupRole)
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