package cn.tabidachi.security.jwt

import io.ktor.server.auth.*

class UserPrincipal(val userId: Long) : Principal