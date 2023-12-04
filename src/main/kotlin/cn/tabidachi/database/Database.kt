package cn.tabidachi.database

import cn.tabidachi.database.table.*
import cn.tabidachi.system.PropertyPath
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Schema
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
    val driver: String
    val url: String
    val name: String
    val user: String
    val password: String
    with(environment.config) {
        driver = property(PropertyPath.Database.DRIVER).getString()
        url = property(PropertyPath.Database.URL).getString()
        name = property(PropertyPath.Database.NAME).getString()
        user = property(PropertyPath.Database.USER).getString()
        password = property(PropertyPath.Database.PASSWORD).getString()
    }
    val tables = arrayOf(
        UserTable,
        SessionTable,
        SessionUserTable,
        MessageTable,
        RelationTable,
        DeviceTable,
        GroupRoleTable,
        ChannelRoleTable
    )
    kotlin.runCatching {
        Database.connect(url, driver, user, password)
        transaction {
            SchemaUtils.createSchema(Schema(name))
            SchemaUtils.setSchema(Schema(name))
            SchemaUtils.createMissingTablesAndColumns(*tables)
        }
        Database.connect("$url/$name", driver, user, password)
    }.onFailure {
        it.printStackTrace()
    }
}