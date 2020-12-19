package com.brtvsk.auth.repository.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object VisitedGeos : Table() {
    val userId : Column<Int> = VisitedGeos.integer("userId").references(Users.userId)
    val geoId = text("geoId")
    val counter : Column<Int> = VisitedGeos.integer("counter")
}