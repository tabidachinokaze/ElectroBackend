package cn.tabidachi.service.impl

import cn.tabidachi.database.dao.UserDao
import cn.tabidachi.ext.ELECTRO
import cn.tabidachi.model.ResponseData
import cn.tabidachi.model.response.AuthResponse
import cn.tabidachi.security.code.Verifiable
import cn.tabidachi.security.code.VerifyResult
import cn.tabidachi.security.jwt.SimpleJWT
import cn.tabidachi.service.AuthService
import io.ktor.http.*
import io.ktor.util.*

class AuthServiceImpl(
    private val userDao: UserDao, private val jwt: SimpleJWT, private val verifiable: Verifiable
) : AuthService {
    override fun auth(email: String, password: String, code: String?): ResponseData<String?> {
        return when {
            password.length < 8 || password.length > 64 -> ResponseData(
                HttpStatusCode.BadRequest, "密码太长或太短", null
            )

            else -> when (userDao.findByEmail(email)) {
                null -> when { // 未注册
                    code.isNullOrEmpty() -> ResponseData(HttpStatusCode.BadRequest, "账号未注册", null)
                    else -> when (verifiable.verify(email, code)) {
                        VerifyResult.CORRECT -> {
                            val user = userDao.saveUser(
                                "", password, email, avatar = Url(
                                    URLBuilder(
                                        protocol = URLProtocol.ELECTRO,
                                        pathSegments = listOf("avatar", "transparent_akkarin.jpg")
                                    )
                                ).toString()
                            )
                            userDao.updateUser(user.uid, "electro_${user.uid}")
                            ResponseData(HttpStatusCode.Created, "注册成功", jwt.sign(user.uid))
                        }

                        VerifyResult.ERROR -> ResponseData(HttpStatusCode.BadRequest, "验证码错误", null)
                        VerifyResult.EXPIRED -> ResponseData(HttpStatusCode.BadRequest, "验证码已过期", null)
                    }
                }

                else -> { // 已注册
                    when (val user = userDao.findByUserPassword(email, password)) {
                        null -> ResponseData(HttpStatusCode.BadRequest, "密码错误", null)
                        else -> ResponseData(HttpStatusCode.OK, "登录成功", jwt.sign(user.uid))
                    }
                }
            }
        }
    }

    override fun login(email: String, password: String): ResponseData<AuthResponse?> {
        return when (val user = userDao.findByUserPassword(email, password)) {
            null -> ResponseData(HttpStatusCode.BadRequest, "账号或密码错误", null)
            else -> ResponseData(HttpStatusCode.OK, "登录成功", AuthResponse(user.uid, jwt.sign(user.uid)))
        }
    }

    override fun register(email: String, password: String, code: String): ResponseData<AuthResponse?> {
        return when (val user = userDao.findByUserPassword(email, password)) {
            null -> when (verifiable.verify(email, code)) {
                VerifyResult.CORRECT -> {
                    val registerUser = userDao.saveUser(
                        generateNonce(), password, email, avatar = Url(
                            URLBuilder(
                                protocol = URLProtocol.ELECTRO,
                                pathSegments = listOf("avatar", "transparent_akkarin.jpg")
                            )
                        ).toString()
                    )
                    ResponseData(
                        HttpStatusCode.Created,
                        "注册成功",
                        AuthResponse(registerUser.uid, jwt.sign(registerUser.uid))
                    )
                }

                VerifyResult.ERROR -> ResponseData(HttpStatusCode.BadRequest, "验证码错误", null)
                VerifyResult.EXPIRED -> ResponseData(HttpStatusCode.BadRequest, "验证码已过期", null)
            }

            else -> ResponseData(
                HttpStatusCode.BadRequest, "用户已存在", AuthResponse(user.uid, jwt.sign(user.uid))
            )
        }
    }
}