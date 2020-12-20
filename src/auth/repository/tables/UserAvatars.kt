package com.brtvsk.auth.repository.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object UserAvatars : Table() {
    val userId : Column<Int> = integer("user_id").references(Users.userId)
    val avatar = text("avatar")
}