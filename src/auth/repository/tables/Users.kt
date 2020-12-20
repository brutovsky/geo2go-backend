package com.brtvsk.auth.repository.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val userId : Column<Int> = integer("id").autoIncrement().primaryKey()
    val email = text("email").uniqueIndex()
    val displayName = text("display_name").uniqueIndex()
    val avatar = text("avatar")
    val passwordHash = text("password_hash")
}
