package moe.tabidachi.service

import moe.tabidachi.database.model.User
import moe.tabidachi.model.ResponseData
import moe.tabidachi.model.UserQuery

interface UserService {
    fun getUserInfo(userId: Long): ResponseData<User>
    fun deleteUser(userId: Long): ResponseData<Long>
    fun updateUser(
        userId: Long,
        username: String? = null,
        password: String? = null,
        email: String? = null,
        avatar: String? = null
    ): ResponseData<Long>

    fun queryUserById(userId: Long): ResponseData<List<UserQuery>>
    fun queryUserByUsernameRegex(username: String): ResponseData<List<UserQuery>>
    fun queryUserByEmail(email: String): ResponseData<String?>
}