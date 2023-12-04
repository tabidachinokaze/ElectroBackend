package moe.tabidachi.exception

import io.ktor.http.*

abstract class Exception : Throwable() {
    abstract val status: HttpStatusCode
}