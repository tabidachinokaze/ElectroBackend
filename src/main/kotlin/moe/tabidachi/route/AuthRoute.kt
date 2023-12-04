package moe.tabidachi.route

import moe.tabidachi.exception.BadRequestException
import moe.tabidachi.exception.TooManyRequestsException
import moe.tabidachi.ext.isEmail
import moe.tabidachi.model.response.Response
import moe.tabidachi.model.reuqest.LoginRequest
import moe.tabidachi.model.reuqest.RegisterRequest
import moe.tabidachi.security.access.AccessControl
import moe.tabidachi.service.AuthService
import moe.tabidachi.service.UserService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.auth() {
    val authService: AuthService by inject()
    val accessControl: AccessControl by inject()
    val userService: UserService by inject()

    post("/login") {
        val (email, password) = kotlin.runCatching {
            call.receive<LoginRequest>()
        }.getOrElse {
            throw BadRequestException("参数错误")
        }
        if (!email.isEmail()) {
            throw BadRequestException("无效的邮箱地址")
        }
        if (!accessControl.isAllow(email)) {
            throw TooManyRequestsException("请求过多")
        }
        val (status, message, response) = authService.login(email, password)
        call.respond(status, Response(status.value, message, response))
    }
    post("/register") {
        val (email, password, code) = kotlin.runCatching {
            call.receive<RegisterRequest>()
        }.getOrElse {
            throw BadRequestException("参数错误")
        }
        if (!email.isEmail()) {
            throw BadRequestException("无效的邮箱地址")
        }
        if (!accessControl.isAllow(email)) {
            throw TooManyRequestsException("请求过多")
        }
        val (status, message, response) = authService.register(email, password, code)
        call.respond(status, Response(status.value, message, response))
    }
    get("/check/{email}") {
        val email = call.parameters["email"] ?: throw BadRequestException("参数错误")
        val (status, message, data) = userService.queryUserByEmail(email)
        call.respond(status, Response<String?>(status.value, message, data))
    }
}