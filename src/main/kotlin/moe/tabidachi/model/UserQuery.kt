package moe.tabidachi.model

import kotlinx.serialization.Serializable

@Serializable
class UserQuery(
    val uid: Long,
    val username: String,
    val avatar: String
)