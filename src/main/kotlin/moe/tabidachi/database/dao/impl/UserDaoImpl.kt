package moe.tabidachi.database.dao.impl

import moe.tabidachi.database.dao.UserDao
import moe.tabidachi.database.entity.UserEntity
import moe.tabidachi.database.model.User
import moe.tabidachi.database.model.User.Companion.toUser
import moe.tabidachi.database.table.UserTable
import moe.tabidachi.security.jwt.SimpleJWT
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.statements.UpdateStatement
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class UserDaoImpl(
    private val jwt: SimpleJWT
) : UserDao {
    override fun saveUser(username: String, password: String, email: String, avatar: String): User = transaction {
        UserEntity.new {
            this.username = username
            this.password = jwt.encrypt(password)
            this.email = email
            this.avatar = avatar
        }.toUser()
    }

    override fun removeUserById(userId: Long): Boolean = transaction {
        UserTable.deleteWhere { UserTable.id eq userId } > 0
    }

    override fun updateUser(
        userId: Long, username: String?, password: String?, email: String?, avatar: String?
    ): Boolean = transaction {
        UserTable.update(where = {
            UserTable.id eq userId
        }) {
            username?.let(it, UserTable.username)
            password?.let { password ->
                jwt.encrypt(password).let(it, UserTable.password)
            }
            email?.let(it, UserTable.email)
            avatar?.let(it, UserTable.avatar)
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
            (UserTable.email eq email) and (UserTable.password eq jwt.encrypt(password))
        }.singleOrNull()?.toUser()
    }

    override fun findByUsernameRegex(regex: Regex): List<User> = transaction {
        UserEntity.find { UserTable.username regexp regex.pattern }.map { it.toUser() }
    }

    private fun <T> T.let(statement: UpdateStatement, column: Column<T>) {
        statement[column] = this
    }
}