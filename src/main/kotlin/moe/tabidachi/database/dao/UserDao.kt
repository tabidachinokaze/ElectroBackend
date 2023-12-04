package moe.tabidachi.database.dao

import moe.tabidachi.database.model.User

interface UserDao {
    fun saveUser(username: String, password: String, email: String, avatar: String): User
    fun removeUserById(userId: Long): Boolean
    fun updateUser(userId: Long, username: String? = null, password: String? = null, email: String? = null, avatar: String? = null): Boolean
    fun findById(userId: Long): User?
    fun findByEmail(email: String): User?
    fun findByUserPassword(email: String, password: String): User?
    fun findByUsernameRegex(regex: Regex): List<User>
}