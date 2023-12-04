package moe.tabidachi.plugins

import moe.tabidachi.di.KoinModule
import moe.tabidachi.system.PropertyPath
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger

fun Application.configureDI() {
    val jwt = with(PropertyPath.Jwt) {
        arrayOf(DOMAIN, AUDIENCE, REALM, SECRET)
    }
    val smtp = with(PropertyPath.Smtp) {
        arrayOf(HOST, PORT, USERNAME, PASSWORD)
    }
    install(Koin) {
        jwt.forEach {
            koin.setProperty(it, environment.config.property(it).getString())
        }
        smtp.forEach {
            koin.setProperty(it, environment.config.property(it).getString())
        }
        logger(SLF4JLogger())
        modules(KoinModule.all)
    }
}