package cn.tabidachi.di

import cn.tabidachi.database.dao.UserDao
import cn.tabidachi.database.dao.impl.UserDaoImpl
import cn.tabidachi.security.access.AccessControl
import cn.tabidachi.security.code.SecurityCode
import cn.tabidachi.security.code.Verifiable
import cn.tabidachi.security.jwt.ElectroJWT
import cn.tabidachi.security.jwt.SimpleJWT
import cn.tabidachi.service.AuthService
import cn.tabidachi.service.impl.AuthServiceImpl
import cn.tabidachi.system.ElectroEmail
import cn.tabidachi.system.PropertyPath
import org.koin.dsl.module
import java.time.Duration

object KoinModule {
    private val dao = module {
        single<UserDao> {
            UserDaoImpl()
        }
    }
    private val service = module {
        single<AuthService> {
            AuthServiceImpl(get(), get(), get())
        }
    }
    private val security = module {
        single<SimpleJWT> {
            ElectroJWT(
                getProperty(PropertyPath.Jwt.SECRET),
                getProperty(PropertyPath.Jwt.DOMAIN),
                arrayOf(getProperty(PropertyPath.Jwt.AUDIENCE)),
            )
        }
        single<Verifiable> {
            SecurityCode(Duration.ofMinutes(15))
        }
        single {
            AccessControl(Duration.ofMinutes(1), 10)
        }
    }
    private val system = module {
        single {
            ElectroEmail(
                getProperty(PropertyPath.Smtp.HOST),
                getProperty(PropertyPath.Smtp.USERNAME),
                getProperty(PropertyPath.Smtp.PASSWORD),
            )
        }
    }
    val all = module {
        includes(dao, security, service, system)
    }
}