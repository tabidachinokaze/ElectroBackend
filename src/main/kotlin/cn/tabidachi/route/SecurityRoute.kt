package cn.tabidachi.route

import cn.tabidachi.exception.*
import cn.tabidachi.ext.isEmail
import cn.tabidachi.model.response.EmptyData
import cn.tabidachi.model.response.Response
import cn.tabidachi.model.reuqest.CaptchaRequest
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
    route("/captcha") {
        post {
            val (data, method, type) = kotlin.runCatching {
                call.receive<CaptchaRequest>().also {
                    println(it)
                }
            }.getOrElse {
                throw BadRequestException("参数错误")
            }
            if (!data.isEmail()) {
                throw BadRequestException("无效的邮箱地址")
            }
            if (!accessControl.isAllow(data)) {
                throw TooManyRequestsException("请求过多")
            }
            val code = securityCode.generate(data)
            electroEmail.sendSecurityCode(data, code).onSuccess {
                call.respond(HttpStatusCode.OK, Response(HttpStatusCode.OK.value, "发送成功", EmptyData))
            }.onFailure {
                throw InternalServerErrorException(it.message)
            }
        }
    }
}