package cn.tabidachi.security.jwt

import io.ktor.server.auth.*

data class UserPrincipal(val userId: Long) : Principal