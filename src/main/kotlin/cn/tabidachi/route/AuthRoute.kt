package cn.tabidachi.route

import cn.tabidachi.exception.BadRequestException
import cn.tabidachi.exception.TooManyRequestsException
import cn.tabidachi.ext.isEmail
import cn.tabidachi.model.response.TokenResponse
import cn.tabidachi.model.reuqest.AuthRequest
import cn.tabidachi.security.access.AccessControl
import cn.tabidachi.service.AuthService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.auth() {
    val authService: AuthService by inject()
    val accessControl: AccessControl by inject()

    route("/auth") {
        post {
            val (email, password, code) = kotlin.runCatching {
                call.receive<AuthRequest>()
            }.getOrElse {
                throw BadRequestException("参数错误")
            }
            if (!email.isEmail()) {
                throw BadRequestException("无效的邮箱地址")
            }
            if (!accessControl.isAllow(email)) {
                throw TooManyRequestsException("请求过多")
            }
            val (status, message, token) = authService.auth(email, password, code)
            call.respond(status, TokenResponse(status.value, message, token))
        }
    }
}