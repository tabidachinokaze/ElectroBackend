package cn.tabidachi.database.model

import cn.tabidachi.database.entity.UserEntity

data class User(
    val userId: Long,
    val username: String,
    val email: String
) {
    companion object {
        fun UserEntity.toUser(): User = User(
            userId = id.value,
            username = username,
            email = email
        )
    }
}