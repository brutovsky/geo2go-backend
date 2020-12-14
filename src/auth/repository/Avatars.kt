package com.brtvsk.auth.repository

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Avatars : Table() {
    val userId : Column<Int> = integer("userId").references(Users.userId)
    val avatar = varchar("avatar", 256)
}