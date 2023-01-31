package cn.tabidachi.plugins

import cn.tabidachi.database.dao.UserDao
import cn.tabidachi.security.jwt.ElectroJWT
import cn.tabidachi.security.jwt.SimpleJWT
import cn.tabidachi.security.jwt.UserPrincipal
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    val userDao: UserDao by inject()
    val jwt: SimpleJWT by inject()
    authentication {
        jwt {
            val jwtAudience = this@configureSecurity.environment.config.property("jwt.audience").getString()
            realm = this@configureSecurity.environment.config.property("jwt.realm").getString()
            /*
            verifier(
                JWT
                    .require(Algorithm.HMAC256("secret"))
                    .withAudience(jwtAudience)
                    .withIssuer(this@configureSecurity.environment.config.property("jwt.domain").getString())
                    .build()
            )
            */
            verifier(jwt.verifier)
            validate { credential ->
//                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
                val uid = credential.payload.getClaim(ElectroJWT.UID_CLAIM).asLong()
                val user = userDao.findById(uid)
                if (user != null) UserPrincipal(uid) else null
            }
        }
    }
}
