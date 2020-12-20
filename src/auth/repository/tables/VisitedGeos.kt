package com.brtvsk.auth.repository.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object VisitedGeos : Table() {
    val userId : Column<Int> = integer("user_id").references(Users.userId)
    val geoId = text("geo_id")
    val counter = integer("counter")
}