package cn.tabidachi.di

import cn.tabidachi.WebSocketClient
import cn.tabidachi.database.dao.*
import cn.tabidachi.database.dao.impl.*
import cn.tabidachi.security.access.AccessControl
import cn.tabidachi.security.code.SecurityCode
import cn.tabidachi.security.code.Verifiable
import cn.tabidachi.security.jwt.ElectroJWT
import cn.tabidachi.security.jwt.SimpleJWT
import cn.tabidachi.service.*
import cn.tabidachi.service.impl.*
import cn.tabidachi.system.ElectroEmail
import cn.tabidachi.system.PropertyPath
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