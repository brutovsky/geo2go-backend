package com.brtvsk.auth.repository

import com.brtvsk.auth.repository.tables.*
import com.brtvsk.geo.models.geoTags
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        Database.connect(hikari())

        transaction {
            SchemaUtils.create(Users)
            SchemaUtils.create(Avatars)
            SchemaUtils.create(VisitedGeos)
            if(!GeoTags.exists()){
                SchemaUtils.create(GeoTags)
                GeoTags.batchInsert(geoTags) { (name, type) ->
                    this[GeoTags.name] = name
                    this[GeoTags.type] = type?.name
                }
            }
            SchemaUtils.create(UserTags)
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
