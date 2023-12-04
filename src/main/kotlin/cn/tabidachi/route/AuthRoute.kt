package cn.tabidachi.route

import cn.tabidachi.exception.BadRequestException
import cn.tabidachi.exception.TooManyRequestsException
import cn.tabidachi.ext.isEmail
import cn.tabidachi.model.response.Response
import cn.tabidachi.model.reuqest.LoginRequest
import cn.tabidachi.model.reuqest.RegisterRequest
import cn.tabidachi.security.access.AccessControl
import cn.tabidachi.service.AuthService
import cn.tabidachi.service.UserService
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