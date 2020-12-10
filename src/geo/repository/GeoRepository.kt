package com.brtvsk.geo.repository

import com.brtvsk.geo.MongoDB.MongoDB
import com.brtvsk.geo.models.Geo
import org.litote.kmongo.getCollection

class GeoRepository:Repository{

    val geo = MongoDB.mongoDB.getCollection<Geo>(Geos.COL_NAME)

    override fun addGeo(position: String): Geo? {
        TODO("Not yet implemented")
    }

    override fun findGeo(geoId: Int): Geo? {
        TODO("Not yet implemented")
    }

}