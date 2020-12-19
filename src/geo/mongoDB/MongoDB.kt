package com.brtvsk.geo.MongoDB

import com.brtvsk.auth.repository.Avatars
import com.brtvsk.auth.repository.DatabaseFactory
import com.brtvsk.auth.repository.Users
import com.brtvsk.geo.models.Geo
import com.brtvsk.geo.models.GeoTag
import com.brtvsk.geo.models.GeoType
import com.brtvsk.geo.models.geoTags
import com.brtvsk.geo.repository.Geos
import com.brtvsk.geo.repository.Geos.COL_GEO_TAGS
import com.google.gson.Gson
import com.mongodb.client.MongoDatabase
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

object MongoDB {

    const val DB_NAME = "geo"
    val mongoDB:MongoDatabase by lazy {
        val kMongoClient = KMongo.createClient()
        kMongoClient.getDatabase(DB_NAME)
    }

    fun init() {
        val col = MongoDB.mongoDB.getCollection<GeoTag>(COL_GEO_TAGS)
        if(col.countDocuments() <= 0){
            val res = col.insertMany(geoTags)
        }
    }

}

/*
col.insertOne(Jedi("Luke Skywalker", 19))
col.insertOne(Jedi("Luke Skywalker", 19))

val yoda : Jedi? = col.findOne(Jedi::name eq "Yoda")
val luke : Jedi? = col.findOne(Jedi::name eq "Luke Skywalker")
*/