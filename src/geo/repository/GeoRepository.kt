package com.brtvsk.geo.repository

import com.brtvsk.geo.MongoDB.MongoDB
import com.brtvsk.geo.models.Geo
import org.litote.kmongo.*
import org.litote.kmongo.id.ObjectIdGenerator

class GeoRepository:Repository{

    private val geo = MongoDB.mongoDB.getCollection<Geo>(Geos.COL_NAME)

    override fun addGeo(userId:Int, position: String, description: String): Geo? {
        val id = ObjectIdGenerator.newObjectId<Geo>().id
        geo.insertOne(Geo(id,userId,position,description))
        return geo.findOne(Geo::_id eq id)
    }

    override fun findGeo(position:String): Geo? {
        return geo.findOne(Geo::position eq position)
    }

}