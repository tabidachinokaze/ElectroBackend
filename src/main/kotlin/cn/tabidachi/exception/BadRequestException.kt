package cn.tabidachi.exception

import io.ktor.http.*

class BadRequestException(override val message: String?) : Exception() {
    override val status: HttpStatusCode = HttpStatusCode.BadRequest
}