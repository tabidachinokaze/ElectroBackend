package cn.tabidachi.model.response

import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
class InternalServerErrorResponse(
    override val code: Int = HttpStatusCode.InternalServerError.value,
    override val message: String
) : Response