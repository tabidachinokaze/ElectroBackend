package cn.tabidachi.database.dao.impl

import cn.tabidachi.database.dao.UserDao
import cn.tabidachi.database.entity.UserEntity
import cn.tabidachi.database.model.User
import cn.tabidachi.database.model.User.Companion.toUser
import cn.tabidachi.database.table.UserTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class UserDaoImpl : UserDao {
    override fun saveUser(username: String, password: String, email: String): User = transaction {
        UserEntity.new {
            this.username = username
            this.password = password
            this.email = email
        }.toUser()
    }

    override fun removeUserById(userId: Long): Boolean = transaction {
        UserTable.deleteWhere { UserTable.id eq userId } > 0
    }

    override fun updateUser(userId: Long, username: String?, password: String?, email: String?): Boolean = transaction {
        UserTable.update(
            where = {
                UserTable.id eq userId
            }
        ) {
            username?.let(it, UserTable.username)
            password?.let(it, UserTable.password)
            email?.let(it, UserTable.email)
        } > 0
    }

    override fun findById(userId: Long): User? = transaction {
        UserEntity.findById(userId)?.toUser()
    }

    override fun findByEmail(email: String): User? = transaction {
        UserEntity.find {
            UserTable.email eq email
        }.singleOrNull()?.toUser()
    }

    override fun findByUserPassword(email: String, password: String): User? = transaction {
        UserEntity.find {
            (UserTable.email eq email) and (UserTable.password eq password)
        }.singleOrNull()?.toUser()
    }

    private fun <T> T.let(statement: UpdateStatement, column: Column<T>) {
        statement[column] = this
    }
}