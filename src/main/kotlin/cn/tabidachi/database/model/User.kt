package cn.tabidachi.database.model

import cn.tabidachi.database.entity.UserEntity
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val uid: Long,
    val username: String,
    val email: String,
    val avatar: String
) {
    constructor(entity: UserEntity) : this(
        uid = entity.id.value,
        username = entity.username,
        email = entity.email,
        avatar = entity.avatar
    )
    companion object {
        fun UserEntity.toUser(): User = User(
            uid = id.value,
            username = username,
            email = email,
            avatar = avatar
        )
    }
}