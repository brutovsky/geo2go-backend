package com.brtvsk.auth.repository

import com.brtvsk.auth.repository.tables.GeoTags
import com.brtvsk.geo.models.GeoTag
import com.brtvsk.geo.models.GeoType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll

class GeoTagsRepository {

    suspend fun getAll() = DatabaseFactory.dbQuery {
        GeoTags.selectAll().mapNotNull { rowToTag(it) }
    }

    private fun rowToTag(row: ResultRow?): GeoTag?{
        if (row == null) {
            return null
        }
        return GeoTag(
            id = row[GeoTags.id],
            tag = row[GeoTags.name],
            type = GeoType.valueOf(row[GeoTags.type].toString()),
        )
    }

}