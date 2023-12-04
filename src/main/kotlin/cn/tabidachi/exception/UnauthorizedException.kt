package cn.tabidachi.exception

import io.ktor.http.*

class UnauthorizedException(override val message: String?) : Exception() {
    override val status: HttpStatusCode = HttpStatusCode.Unauthorized
}