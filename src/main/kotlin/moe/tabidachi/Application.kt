package moe.tabidachi

import moe.tabidachi.database.configureDatabase
import io.ktor.server.application.*
import moe.tabidachi.plugins.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureDI()
    configureDatabase()
    configureSecurity()
    configureSockets()
    configureSerialization()
    configureRouting()
//    configureReteLimit()
//    configureCachingHeaders()
}
