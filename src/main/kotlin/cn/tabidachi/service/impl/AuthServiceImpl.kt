package cn.tabidachi.service.impl

import cn.tabidachi.database.dao.UserDao
import cn.tabidachi.model.ResponseData
import cn.tabidachi.security.code.Verifiable
import cn.tabidachi.security.code.VerifyResult
import cn.tabidachi.security.jwt.SimpleJWT
import cn.tabidachi.service.AuthService
import io.ktor.http.*

class AuthServiceImpl(
    private val userDao: UserDao,
    private val jwt: SimpleJWT,
    private val verifiable: Verifiable
) : AuthService {
    override fun auth(email: String, password: String, code: String): ResponseData<String?> {
        return when {
            password.length < 8 || password.length > 64 -> ResponseData(HttpStatusCode.BadRequest, "密码太长或太短", null)
            else -> when (userDao.findByEmail(email)) {
                null -> when { // 未注册
                    code.isEmpty() -> ResponseData(HttpStatusCode.BadRequest, "账号未注册", null)
                    else -> when (verifiable.verify(email, code)) {
                        VerifyResult.CORRECT -> {
                            val user = userDao.saveUser("", jwt.encrypt(password), email)
                            userDao.updateUser(user.userId, "electro_${user.userId}")
                            ResponseData(HttpStatusCode.Created, "注册成功", jwt.sign(user.userId))
                        }

                        VerifyResult.ERROR -> ResponseData(HttpStatusCode.BadRequest, "验证码错误", null)
                        VerifyResult.EXPIRED -> ResponseData(HttpStatusCode.BadRequest, "验证码已过期", null)
                    }
                }

                else -> { // 已注册
                    when (val user = userDao.findByUserPassword(email, jwt.encrypt(password))) {
                        null -> ResponseData(HttpStatusCode.BadRequest, "密码错误", null)
                        else -> ResponseData(HttpStatusCode.OK, "登录成功", jwt.sign(user.userId))
                    }
                }
            }
        }
    }
}