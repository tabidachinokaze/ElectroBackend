package cn.tabidachi.plugins

import cn.tabidachi.exception.BadRequestException
import cn.tabidachi.exception.TooManyRequestsException
import cn.tabidachi.model.response.InternalServerErrorResponse
import cn.tabidachi.model.response.MessageResponse
import cn.tabidachi.route.auth
import cn.tabidachi.route.security
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(StatusPages) {
        exception { call: ApplicationCall, cause: Throwable ->
            when (cause) {
                is BadRequestException -> call.respond(
                    cause.status,
                    MessageResponse(cause.status.value, cause.message)
                )

                is TooManyRequestsException -> call.respond(
                    cause.status,
                    MessageResponse(cause.status.value, cause.message)
                )

                else -> call.respond(
                    HttpStatusCode.InternalServerError,
                    InternalServerErrorResponse(
                        message = cause.message ?: HttpStatusCode.InternalServerError.description
                    )
                )
            }
        }
    }

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }

    routing {
        security()
        auth()
    }
}
