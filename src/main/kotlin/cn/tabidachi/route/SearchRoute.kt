package cn.tabidachi.route

import cn.tabidachi.database.entity.SessionEntity
import cn.tabidachi.database.entity.SessionUserEntity
import cn.tabidachi.database.model.Session
import cn.tabidachi.database.table.SessionTable
import cn.tabidachi.database.table.SessionUserTable
import cn.tabidachi.exception.BadRequestException
import cn.tabidachi.ext.regex
import cn.tabidachi.model.response.Response
import cn.tabidachi.model.response.SessionSearchResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.search() {
    get("/search/session/{title}") {
        val title = call.parameters["title"] ?: throw BadRequestException("参数错误")
        val regex = title.regex()
        transaction {
            SessionEntity.find {
                ((SessionTable.type eq SessionTable.SessionType.ROOM) or (SessionTable.type eq SessionTable.SessionType.CHANNEL)) and (SessionTable.title regexp regex.pattern)
            }.map(::Session).map {
                val count = SessionUserEntity.find { SessionUserTable.sid eq it.sid }.count()
                SessionSearchResponse(it.sid, it.type, it.title, it.description, it.image, it.createTime, count.toInt())
            }
        }.let {
            val status = HttpStatusCode.OK
            call.respond(status, Response(status.value, "查询成功", it))
        }
    }
}