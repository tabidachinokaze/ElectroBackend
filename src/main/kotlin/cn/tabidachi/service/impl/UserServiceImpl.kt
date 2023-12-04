package cn.tabidachi.service.impl

import cn.tabidachi.database.dao.UserDao
import cn.tabidachi.database.model.User
import cn.tabidachi.exception.NotFoundException
import cn.tabidachi.model.ResponseData
import cn.tabidachi.model.UserQuery
import cn.tabidachi.service.UserService
import io.ktor.http.*

class UserServiceImpl(
    private val userDao: UserDao
) : UserService {
    override fun getUserInfo(userId: Long): ResponseData<User> {
        val user = userDao.findById(userId) ?: throw NotFoundException("用户不存在")
        return ResponseData(HttpStatusCode.OK, "获取成功", user)
    }

    override fun deleteUser(userId: Long): ResponseData<Long> {
        userDao.removeUserById(userId)
        return ResponseData(HttpStatusCode.OK, "注销成功", userId)
    }

    override fun updateUser(
        userId: Long, username: String?, password: String?, email: String?, avatar: String?
    ): ResponseData<Long> {
        if ((username ?: password ?: email ?: avatar) == null) {
            return ResponseData(HttpStatusCode.BadRequest, "字段全为null", userId)
        }
        userDao.updateUser(userId, username, password, email, avatar)
        return ResponseData(HttpStatusCode.OK, "更新成功", userId)
    }

    override fun queryUserById(userId: Long): ResponseData<List<UserQuery>> {
        val query = userDao.findById(userId)?.let {
            UserQuery(it.uid, it.username, it.avatar)
        }
        val queries = mutableListOf<UserQuery>().apply {
            query?.let(this::add)
        }
        return ResponseData(HttpStatusCode.OK, "查询成功", queries)
    }

    override fun queryUserByUsernameRegex(username: String): ResponseData<List<UserQuery>> {
        val regex = username.toCharArray().joinToString("", "^", ".+") {
            "(?=.*$it)"
        }.toRegex()
        println(regex.pattern)
        val queries = userDao.findByUsernameRegex(regex).map {
            UserQuery(it.uid, it.username, it.avatar)
        }
        return ResponseData(HttpStatusCode.OK, "查询成功", queries)
    }

    override fun queryUserByEmail(email: String): ResponseData<String?> {
        return when (val user = userDao.findByEmail(email)) {
            null -> {
                ResponseData(HttpStatusCode.NotFound, "用户不存在", null)
            }

            else -> {
                ResponseData(HttpStatusCode.OK, "查找成功", user.email)
            }
        }
    }
}