package com.brtvsk.auth.repository.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object GeoTags : Table() {
    val id : Column<Int> = integer("id").autoIncrement().primaryKey()
    val name = text("name")
    val type = integer("type").nullable()
}