package moe.tabidachi.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.ratelimit.*
import kotlin.time.Duration.Companion.seconds

fun Application.configureReteLimit() {
    install(RateLimit) {
        this.global {
            rateLimiter(limit = 50, refillPeriod = 60.seconds)
        }
    }
}