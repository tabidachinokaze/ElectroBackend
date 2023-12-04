package moe.tabidachi.di

import moe.tabidachi.WebSocketClient
import moe.tabidachi.database.dao.*
import moe.tabidachi.database.dao.impl.*
import moe.tabidachi.security.access.AccessControl
import moe.tabidachi.security.code.SecurityCode
import moe.tabidachi.security.code.Verifiable
import moe.tabidachi.security.jwt.ElectroJWT
import moe.tabidachi.security.jwt.SimpleJWT
import moe.tabidachi.service.*
import moe.tabidachi.service.impl.*
import moe.tabidachi.system.ElectroEmail
import moe.tabidachi.system.PropertyPath
import org.koin.dsl.module
import java.time.Duration

object KoinModule {
    private val dao = module {
        single<UserDao> {
            UserDaoImpl(get())
        }
        single<ChatDao> {
            ChatDaoImpl()
        }
        single<SessionUserDao> {
            SessionUserDaoImpl()
        }
        single<MessageDao> {
            MessageDaoImpl()
        }
        single<RelationDao> {
            RelationDaoImpl()
        }
        single<DeviceDao> {
            DeviceDaoImpl()
        }
    }
    private val service = module {
        single<AuthService> {
            AuthServiceImpl(get(), get(), get())
        }
        single<UserService> {
            UserServiceImpl(get())
        }
        single<ChatService> {
            ChatServiceImpl(get(), get(), get(), get(), get())
        }
        single<ChatUserService> {
            ChatUserServiceImpl(get())
        }
        single<MessageService> {
            MessageServiceImpl(get())
        }
        single<ChatsService> {
            ChatsServiceImpl(get(), get(), get(), get())
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
                getProperty(PropertyPath.Smtp.PORT),
                getProperty(PropertyPath.Smtp.USERNAME),
                getProperty(PropertyPath.Smtp.PASSWORD),
            )
        }
        single {
            WebSocketClient(get(), get(), get())
        }
    }
    val all = module {
        includes(dao, security, service, system)
    }
}