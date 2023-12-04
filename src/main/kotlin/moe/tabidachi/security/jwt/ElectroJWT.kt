package moe.tabidachi.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * @param secret 验证或签名中使用的密钥
 */
class ElectroJWT(
    secret: String,
    private val issuer: String? = null,
    private val audience: Array<out String?> = arrayOf()
) : SimpleJWT {
    private val algorithm = Algorithm.HMAC256(secret)
    private val mac = Mac.getInstance(algorithm.toString())

    init {
        mac.init(SecretKeySpec(secret.toByteArray(), algorithm.toString()))
    }

    override val verifier: JWTVerifier = with(JWT.require(algorithm)) {
        issuer?.let(::withIssuer)
        withAudience(*audience)
        build()
    }

    override fun sign(uid: Long, block: JWTCreator.Builder.() -> Unit): String {
        val builder = JWT.create().withClaim(UID_CLAIM, uid).apply {
            issuer?.let(::withIssuer)
            withAudience(*audience)
        }
        builder.apply(block)
        return builder.sign(algorithm)
    }

    override fun encrypt(value: String): String = hex(mac.doFinal(value.toByteArray()))

    companion object {
        const val UID_CLAIM = "uid"
    }
}