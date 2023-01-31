package cn.tabidachi.model.response

import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    override val code: Int,
    override val message: String?
) : Response
