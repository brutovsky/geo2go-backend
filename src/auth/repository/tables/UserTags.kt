package com.brtvsk.auth.repository.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object UserTags : Table() {
    val userId : Column<Int> = integer("user_id").references(Users.userId)
    val tagId : Column<Int> = integer("tag_id").references(GeoTags.id)
}