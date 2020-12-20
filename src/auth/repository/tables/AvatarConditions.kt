package com.brtvsk.auth.repository.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object AvatarConditions : Table("avatar_conditions") {
    val id : Column<Int> = integer("id").autoIncrement().primaryKey()
    val tagId : Column<Int> = integer("tag_id").references(GeoTags.id)
    val avatar = text("avatar").uniqueIndex()
    val target = integer("target")
}