package cn.tabidachi.route

import cn.tabidachi.database.entity.UserEntity
import cn.tabidachi.database.model.User
import cn.tabidachi.exception.BadRequestException
import cn.tabidachi.exception.UnauthorizedException
import cn.tabidachi.ext.isUidQuery
import cn.tabidachi.ext.uid
import cn.tabidachi.model.response.EmptyData
import cn.tabidachi.model.response.Response
import cn.tabidachi.model.response.emptyData
import cn.tabidachi.model.reuqest.UserUpdateRequest
import cn.tabidachi.security.jwt.UserPrincipal
import cn.tabidachi.service.UserService
import io.ktor.http.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.ktor.ext.inject

fun Route.user() {
    val userService: UserService by inject()
    route("/user") {
        get {
            val (userId) = call.principal<UserPrincipal>() ?: throw UnauthorizedException("身份未验证")
            val (status, message, user) = userService.getUserInfo(userId)
            call.respond(status, Response(status.value, message, user))
        }
        delete {
            val (userId) = call.principal<UserPrincipal>() ?: throw UnauthorizedException("身份未验证")
            val (status, message, data) = userService.deleteUser(userId)
            call.respond(status, Response(status.value, message, data))
        }
        patch {
            val (userId) = call.principal<UserPrincipal>() ?: throw UnauthorizedException("身份未验证")
            val (username, password, email, avatar) = kotlin.runCatching {
                call.receive<UserUpdateRequest>()
            }.getOrElse {
                throw BadRequestException("参数错误")
            }
            val (status, message, data) = userService.updateUser(userId, username, password, email, avatar)
            call.respond(status, Response(status.value, message, data))
        }
        route("/{target}") {
//            install(CachingHeaders) {
//                options { _, _ ->
//                    CachingOptions(
//                        CacheControl.MaxAge(
//                            maxAgeSeconds = 60,
//                            proxyMaxAgeSeconds = 60
//                        )
//                    )
//                }
//            }
            get {
                val target = call.parameters["target"]?.toLong() ?: throw Exception("参数错误")
                uid
                transaction {
                    UserEntity.findById(target)?.let(::User)
                }?.let {
                    val statusCode = HttpStatusCode.OK
                    call.respond(statusCode, Response(statusCode.value, "查询成功", it))
                } ?: call.respond(
                    HttpStatusCode.NotFound,
                    Response(HttpStatusCode.NotFound.value, "查询失败", emptyData<User>())
                )
            }
        }
        get("/query/{query}") {
            val params = call.parameters["query"] ?: throw Exception("参数错误")
            val (userId) = call.principal<UserPrincipal>() ?: throw UnauthorizedException("身份未验证")
            if (params.isUidQuery()) {
                params.filter { it.isDigit() }
            }
            val (status, message, data) = when (params.isUidQuery()) {
                true -> {
                    userService.queryUserById(params.filter { it.isDigit() }.toLong())
                }

                false -> {
                    userService.queryUserByUsernameRegex(params)
                }
            }
            call.respond(status, Response(status.value, message, data))
        }
    }
}