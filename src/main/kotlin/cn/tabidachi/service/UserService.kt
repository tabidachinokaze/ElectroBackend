package cn.tabidachi.service

import cn.tabidachi.database.model.User
import cn.tabidachi.model.ResponseData
import cn.tabidachi.model.UserQuery

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