package cn.tabidachi.model.reuqest

import kotlinx.serialization.Serializable

@Serializable
data class CaptchaRequest(
    val data: String,
    val method: Method,
    val type: Type
) {
    enum class Method {
        PHONE, EMAIL
    }
    enum class Type {
        LOGIN, REGISTER
    }
}