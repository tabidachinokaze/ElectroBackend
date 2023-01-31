package cn.tabidachi.system

object PropertyPath {
    object Jwt {
        const val DOMAIN = "jwt.domain"
        const val AUDIENCE = "jwt.audience"
        const val REALM = "jwt.realm"
        const val SECRET = "jwt.secret"
    }

    object Database {
        const val DRIVER = "database.driver"
        const val URL = "database.url"
        const val NAME = "database.name"
        const val USER = "database.user"
        const val PASSWORD = "database.password"
    }

    object Smtp {
        const val HOST = "smtp.host"
        const val PORT = "smtp.port"
        const val USERNAME = "smtp.username"
        const val PASSWORD = "smtp.password"
    }
}