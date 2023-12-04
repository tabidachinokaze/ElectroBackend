package cn.tabidachi.model.response

import kotlinx.serialization.Serializable

@Serializable
data class Response<T>(
    val status: Int,
    val message: String,
    val data: T?
)

val EmptyData: String? = null

fun <T> emptyData(): T? = null