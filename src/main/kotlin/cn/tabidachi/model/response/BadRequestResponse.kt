package cn.tabidachi.model.response

import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
data class BadRequestResponse(
    override val code: Int = HttpStatusCode.BadRequest.value,
    override val message: String
) : Response