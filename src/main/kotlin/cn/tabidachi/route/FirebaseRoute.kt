package cn.tabidachi.route

import cn.tabidachi.database.entity.DeviceEntity
import cn.tabidachi.database.entity.UserEntity
import cn.tabidachi.database.table.DeviceTable
import cn.tabidachi.ext.uid
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.firebase() {
    post("/firebase") {
        val token = kotlin.runCatching {
            call.receive<String>()
        }.getOrElse {
            throw it
        }
        val uid = uid
        transaction {
            DeviceEntity.find {
                (DeviceTable.uid eq uid) and (DeviceTable.token eq token)
            }.singleOrNull() ?: DeviceEntity.new {
                UserEntity.findById(uid)?.let {
                    this.uid = it.id
                }
                this.token = token
            }
        }.let {
            call.respond(HttpStatusCode.OK)
        }
    }
}