package cn.tabidachi.exception

import io.ktor.http.*

class TooManyRequestsException(override val message: String?) : Exception() {
    override val status: HttpStatusCode = HttpStatusCode.TooManyRequests
}