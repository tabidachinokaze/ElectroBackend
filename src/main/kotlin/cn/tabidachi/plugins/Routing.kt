package cn.tabidachi.plugins

import cn.tabidachi.exception.BadRequestException
import cn.tabidachi.exception.NotFoundException
import cn.tabidachi.exception.TooManyRequestsException
import cn.tabidachi.exception.UnauthorizedException
import cn.tabidachi.model.response.EmptyData
import cn.tabidachi.model.response.Response
import cn.tabidachi.route.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.event.Level

fun Application.configureRouting() {
    install(StatusPages) {
        exception { call: ApplicationCall, cause: Throwable ->
            when (cause) {
                is BadRequestException -> call.respond(
                    cause.status, Response(cause.status.value, cause.message.toString(), EmptyData)
                )

                is TooManyRequestsException -> call.respond(
                    cause.status, Response(cause.status.value, cause.message.toString(), EmptyData)
                )

                is UnauthorizedException -> call.respond(
                    cause.status, Response(cause.status.value, cause.message.toString(), EmptyData)
                )

                is NotFoundException -> call.respond(
                    cause.status, Response(cause.status.value, cause.message.toString(), EmptyData)
                )

                else -> call.respond(
                    HttpStatusCode.InternalServerError,
                    Response(HttpStatusCode.InternalServerError.value, cause.message.toString(), EmptyData)
                )
            }
        }
    }

    routing {
        static("/") {
            staticBasePackage = "static"
            static("avatar") {
                resource("transparent_akkarin.jpg")
            }
        }
    }

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
    install(CallLogging) {
        level = Level.TRACE
        filter { call -> call.request.path().startsWith("/") }
    }
    routing {
        security()
        auth()
        authenticate {
            user()
            message()
            chat()
            session()
            search()
            relation()
            firebase()
            group()
            channel()
        }
        file()
    }
}
