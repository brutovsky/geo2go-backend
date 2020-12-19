package com.brtvsk.geo.repository

import com.brtvsk.auth.models.VisitedGeo
import com.brtvsk.geo.models.Geo
import com.brtvsk.geo.models.GeoType
import com.brtvsk.geo.models.Point
import com.mongodb.client.FindIterable
import org.bson.types.ObjectId

interface Repository {
    suspend fun addGeo(userId:Int, point: Point,
                       type: GeoType, tags: Set<String>,
                       raiting: Map<Integer, Integer>, description: String): Geo?
    suspend fun findGeo(point:Point): Geo?
    suspend fun findGeo(geoId:ObjectId): Geo?
    suspend fun getAll(userId: Int): List<Geo>
    suspend fun addVisitedGeo(userId: Int, geoId:String): VisitedGeo?
    suspend fun findVisitedGeo(userId: Int, geoId:String): VisitedGeo?
    suspend fun incrementVisitedGeo(userId:Int, geoId: String) : Int
    suspend fun getAllVisited(userId: Int): List<VisitedGeo>
    suspend fun findGeosByIds(geoIds: List<ObjectId>): List<Geo>
}