package cn.tabidachi.model

import io.ktor.http.*

data class ResponseData<T>(
    val status: HttpStatusCode,
    val message: String,
    val data: T
)