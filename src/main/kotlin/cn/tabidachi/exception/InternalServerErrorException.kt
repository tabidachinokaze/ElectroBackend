package cn.tabidachi.exception

import io.ktor.http.*

class InternalServerErrorException(override val message: String?) : Exception() {
    override val status = HttpStatusCode.InternalServerError
}
