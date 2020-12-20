package com.brtvsk.auth.repository.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object AvatarConditions : Table() {
    val conditionId : Column<Int> = integer("id").autoIncrement().primaryKey()
    val tagId : Column<Int> = integer("tag_id").references(GeoTags.id)
    val avatar = text("avatar")
    val target = integer("target")
}