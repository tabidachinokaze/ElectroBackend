package moe.tabidachi.route

import moe.tabidachi.database.entity.RelationEntity
import moe.tabidachi.database.entity.UserEntity
import moe.tabidachi.database.table.RelationTable
import moe.tabidachi.database.table.SessionUserTable
import moe.tabidachi.exception.BadRequestException
import moe.tabidachi.ext.uid
import moe.tabidachi.model.response.EmptyData
import moe.tabidachi.model.response.Response
import moe.tabidachi.model.response.emptyData
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

fun Route.relation() {
    get("/relation/{target}") {
        val target = call.parameters["target"]?.toLong() ?: throw BadRequestException("参数错误")
        transaction {
            RelationEntity.find {
                (RelationTable.src eq uid) and (RelationTable.dst eq target)
            }.singleOrNull()
        }.let {
            val status = if (it == null) HttpStatusCode.NotFound else HttpStatusCode.OK
            call.respond(status, Response(status.value, status.description, it?.state))
        }
    }
    get("/relation/contact") {
        transaction {
            RelationEntity.find {
                (RelationTable.src eq uid) and (RelationTable.state eq RelationTable.State.CONTACT)
            }.map {
                it.dst.value
            }
        }.let {
            val status = HttpStatusCode.OK
            call.respond(status, Response(status.value, "查找成功", it))
        }
    }
    route("/relation/{target}/contact") {
        post {
            val target = call.parameters["target"]?.toLong() ?: throw BadRequestException("参数错误")
            transaction {
                RelationEntity.find {
                    (RelationTable.src eq uid) and (RelationTable.dst eq target)
                }.singleOrNull()?.let {
                    RelationTable.update(where = {
                        (RelationTable.src eq uid) and (RelationTable.dst eq target) and (RelationTable.state neq RelationTable.State.CONTACT)
                    }) {
                        it[state] = RelationTable.State.CONTACT
                    }
                } ?: kotlin.run {
                    RelationEntity.new {
                        UserEntity.findById(uid)?.let {
                            this.src = it.id
                        }
                        UserEntity.findById(target)?.let {
                            this.dst = it.id
                        }
                        this.state = RelationTable.State.CONTACT
                    }
                }
            }.let {
                val status = HttpStatusCode.OK
                call.respond(status, Response(status.value, "添加成功", true))
            }
        }
        delete {
            val target = call.parameters["target"]?.toLong() ?: throw BadRequestException("参数错误")
            transaction {
                RelationTable.deleteWhere {
                    (src eq uid) and (dst eq target) and (state eq RelationTable.State.CONTACT)
                } > 0
            }.let {
                val status = HttpStatusCode.OK
                call.respond(status, Response(status.value, "删除失败", it))
            }
        }
    }
    route("/relation/{target}/block") {
        post {
            val target = call.parameters["target"]?.toLong() ?: throw BadRequestException("参数错误")
            transaction {
                RelationEntity.find {
                    (RelationTable.src eq uid) and (RelationTable.dst eq target)
                }.singleOrNull()?.let {
                    RelationTable.update(where = {
                        (RelationTable.src eq uid) and (RelationTable.dst eq target)
                    }) {
                        it[state] = RelationTable.State.BLOCK
                    } > 0
                } ?: kotlin.run {
                    RelationEntity.new {
                        UserEntity.findById(uid)?.let {
                            this.src = it.id
                        }
                        UserEntity.findById(target)?.let {
                            this.dst = it.id
                        }
                        this.state = RelationTable.State.BLOCK
                    }
                    true
                }
            }.let {
                val status = HttpStatusCode.OK
                call.respond(status, Response(status.value, "success", it))
            }
        }
        delete {
            val target = call.parameters["target"]?.toLong() ?: throw BadRequestException("参数错误")
            transaction {
                RelationTable.update(where = {
                    (RelationTable.src eq uid) and (RelationTable.dst eq target) and (RelationTable.state eq RelationTable.State.BLOCK)
                }) {
                    it[state] = RelationTable.State.NONE
                } > 0
            }.let {
                val status = HttpStatusCode.OK
                call.respond(status, Response(status.value, "success", it))
            }
        }
    }
}