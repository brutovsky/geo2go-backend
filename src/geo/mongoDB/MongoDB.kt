package com.brtvsk.geo.MongoDB

import org.litote.kmongo.KMongo

object MongoDB {

    const val DB_NAME = "geo"
    private val kMongoClient = KMongo.createClient()
    val mongoDB = kMongoClient.getDatabase(DB_NAME)

}

/*
col.insertOne(Jedi("Luke Skywalker", 19))
col.insertOne(Jedi("Luke Skywalker", 19))

val yoda : Jedi? = col.findOne(Jedi::name eq "Yoda")
val luke : Jedi? = col.findOne(Jedi::name eq "Luke Skywalker")
*/