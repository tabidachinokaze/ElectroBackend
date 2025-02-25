package moe.tabidachi.route

import moe.tabidachi.exception.BadRequestException
import moe.tabidachi.ext.uid
import moe.tabidachi.model.response.Response
import moe.tabidachi.service.ChatService
import moe.tabidachi.service.ChatsService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.chat() {
    val chatService: ChatService by inject()
    val chatsService: ChatsService by inject()
    get("/chat-list") {
        val (status, message, data) = chatsService.getChatListByUserId(uid)
        call.respond(status, Response(status.value, message, data))
    }
    route("/chat/{target}") {
        get {
            val target = call.parameters["target"]?.toLong() ?: throw BadRequestException("参数错误")
            val (code, s, chatResponse) = chatService.getChatByPairUser(uid to target, false)
            call.respond(code, Response(code.value, s, chatResponse))
        }
        post {
            val target = call.parameters["target"]?.toLong() ?: throw BadRequestException("参数错误")
            val (code, s, chatResponse) = chatService.getChatByPairUser(uid to target, true)
            call.respond(code, Response(code.value, s, chatResponse))
        }
    }
    get("/chat-id/{target}") {
        val target = call.parameters["target"]?.toLong() ?: throw BadRequestException("参数错误")
        val (code, message, chatId) = chatService.getChatIdByPairUser(uid to target)
        call.respond(code, Response(code.value, message, chatId))
    }
}

