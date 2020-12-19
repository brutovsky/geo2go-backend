package com.brtvsk.geo.MongoDB

import com.brtvsk.geo.models.GeoTag
import com.brtvsk.geo.models.geoTags
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

object MongoDB {

    const val DB_NAME = "geo"
    val mongoDB:MongoDatabase by lazy {
        val kMongoClient = KMongo.createClient()
        kMongoClient.getDatabase(DB_NAME)
    }

}

/*
col.insertOne(Jedi("Luke Skywalker", 19))
col.insertOne(Jedi("Luke Skywalker", 19))

val yoda : Jedi? = col.findOne(Jedi::name eq "Yoda")
val luke : Jedi? = col.findOne(Jedi::name eq "Luke Skywalker")
*/