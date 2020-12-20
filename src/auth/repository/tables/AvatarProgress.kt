package com.brtvsk.auth.repository.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object AvatarProgress : Table("avatar_progress") {
    val userId : Column<Int> = integer("user_id").references(Users.userId).primaryKey(0)
    val avatar = text("avatar").primaryKey(1)
    val counter = integer("counter")
}