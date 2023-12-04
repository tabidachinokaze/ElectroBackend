package moe.tabidachi.database.dao.impl

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.statements.UpdateStatement

fun <T> T.let(statement: UpdateStatement, column: Column<T>) {
    statement[column] = this
}