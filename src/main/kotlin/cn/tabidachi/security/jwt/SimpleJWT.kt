package cn.tabidachi.security.jwt

import com.auth0.jwt.JWTCreator
import com.auth0.jwt.JWTVerifier

interface SimpleJWT {
    val verifier: JWTVerifier
    fun sign(uid: Long, block: JWTCreator.Builder.() -> Unit = {}): String
    fun encrypt(value: String): String
}