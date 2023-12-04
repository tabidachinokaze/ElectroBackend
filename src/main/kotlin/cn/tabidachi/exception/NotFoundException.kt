package cn.tabidachi.exception

import io.ktor.http.*

class NotFoundException(override val message: String?) : Exception() {
    override val status: HttpStatusCode = HttpStatusCode.NotFound
}