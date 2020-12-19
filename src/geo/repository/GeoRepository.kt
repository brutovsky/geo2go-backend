package com.brtvsk.geo.repository

import com.brtvsk.geo.MongoDB.MongoDB
import com.brtvsk.geo.models.Geo
import com.brtvsk.geo.models.GeoType
import com.brtvsk.geo.models.Point
import org.litote.kmongo.*
import org.litote.kmongo.id.ObjectIdGenerator

class GeoRepository:Repository{

    private val geo = MongoDB.mongoDB.getCollection<Geo>(Geos.COL_GEOS)

    override suspend fun addGeo(userId:Int, point: Point,
                                type: GeoType, tags: Set<String>,
                                raiting: Map<Integer, Integer>, description: String): Geo? {
        val id = ObjectIdGenerator.newObjectId<Geo>().id
        geo.insertOne(Geo(id,userId,point,type,tags,raiting, description))
        return geo.findOne(Geo::_id eq id)
    }

    override suspend fun findGeo(point:Point): Geo? {
        return geo.findOne(Geo::point eq point)
    }

    override suspend fun getAll(userId: Int): List<Geo>{
        val found = geo.find(Geo::userId eq userId)
        return found.toList()
    }

}