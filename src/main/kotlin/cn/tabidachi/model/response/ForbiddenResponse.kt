package cn.tabidachi.model.response

import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class ForbiddenResponse(
    override val code: Int = HttpStatusCode.Forbidden.value,
    override val message: String? = HttpStatusCode.Forbidden.description
) : Response