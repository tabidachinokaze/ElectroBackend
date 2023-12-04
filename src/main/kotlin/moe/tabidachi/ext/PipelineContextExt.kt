package moe.tabidachi.ext

import moe.tabidachi.exception.UnauthorizedException
import moe.tabidachi.security.jwt.UserPrincipal
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.util.pipeline.*

inline val PipelineContext<*, ApplicationCall>.uid get() = context.principal<UserPrincipal>()?.userId ?: throw UnauthorizedException("身份未验证")