package cn.tabidachi.route

import cn.tabidachi.exception.*
import cn.tabidachi.ext.isEmail
import cn.tabidachi.model.response.MessageResponse
import cn.tabidachi.model.reuqest.SecurityRequest
import cn.tabidachi.security.access.AccessControl
import cn.tabidachi.security.code.Verifiable
import cn.tabidachi.system.ElectroEmail
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.security() {
    val accessControl: AccessControl by inject()
    val securityCode: Verifiable by inject()
    val electroEmail: ElectroEmail by inject()
    route("/security") {
        post {
            val (email) = kotlin.runCatching {
                call.receive<SecurityRequest>()
            }.getOrElse {
                throw BadRequestException("参数错误")
            }
            if (!email.isEmail()) {
                throw BadRequestException("无效的邮箱地址")
            }
            if (!accessControl.isAllow(email)) {
                throw TooManyRequestsException("请求过多")
            }
            val code = securityCode.generate(email)
            electroEmail.sendSecurityCode(email, code).onSuccess {
                call.respond(HttpStatusCode.OK, MessageResponse(HttpStatusCode.OK.value, "发送成功"))
            }.onFailure {
                throw InternalServerErrorException(it.message)
            }
        }
    }
}