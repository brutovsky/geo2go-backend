package com.brtvsk.auth.repository

import com.brtvsk.auth.repository.tables.*
import com.brtvsk.auth.utils.toGeoTag
import com.brtvsk.auth.utils.toUser
import com.brtvsk.avatar.model.avatarConditions
import com.brtvsk.geo.models.geoTags
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.litote.kmongo.cond

object DatabaseFactory {

    fun init() {
        Database.connect(hikari())

        transaction {
            SchemaUtils.create(Users)
            SchemaUtils.create(UserAvatars)
            SchemaUtils.create(VisitedGeos)
            if(!GeoTags.exists()){
                SchemaUtils.create(GeoTags)
                GeoTags.batchInsert(geoTags) { (name, type) ->
                    this[GeoTags.name] = name
                    this[GeoTags.type] = type?.name
                }
            }
            SchemaUtils.create(UserTags)
            if(!AvatarConditions.exists()){
                SchemaUtils.create(AvatarConditions)
                val conditionsWithTagIds = avatarConditions.map { condition ->
                    condition to GeoTags.select{ ( (GeoTags.name eq (condition.condition.tag)) and (GeoTags.type eq condition.condition.type?.name) ) }.map { it.toGeoTag() }.singleOrNull()
                }
                AvatarConditions.batchInsert(conditionsWithTagIds) { (condition, tagId) ->
                    this[AvatarConditions.avatar] = condition.avatar.name
                    this[AvatarConditions.target] = condition.target
                    this[AvatarConditions.tagId] = tagId?.id ?: -1
                }
            }
            SchemaUtils.create(AvatarConditions)
            SchemaUtils.create(AvatarProgress)
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig()
        config.driverClassName = System.getenv("JDBC_DRIVER")
        config.jdbcUrl = System.getenv("JDBC_DATABASE_URL")
        config.maximumPoolSize = 3
        config.isAutoCommit = false
        config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"

        val user = System.getenv("DB_USER")
        if (user != null) {
            config.username = user
        }
        val password = System.getenv("DB_PASSWORD")
        if (password != null) {
            config.password = password
        }

        //config.password = "your-pass"

        config.validate()
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: () -> T): T =
        withContext(Dispatchers.IO){
            transaction { block() }
        }

}
