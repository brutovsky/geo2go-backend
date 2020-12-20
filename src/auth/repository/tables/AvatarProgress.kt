package com.brtvsk.auth.repository.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object AvatarProgress : Table() {
    val conditionId : Column<Int> = integer("condition_id").references(AvatarConditions.conditionId)
    val userId : Column<Int> = integer("user_id").references(Users.userId)
    val counter = integer("counter")
}