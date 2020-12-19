package com.brtvsk.geo.service

import com.brtvsk.auth.models.User
import com.brtvsk.auth.models.VisitedGeo
import com.brtvsk.auth.repository.GeoTagsRepository
import com.brtvsk.auth.repository.UserRepository
import com.brtvsk.geo.dto.GeoDTO
import com.brtvsk.geo.models.Geo
import com.brtvsk.geo.models.GeoTag
import com.brtvsk.geo.models.Point
import com.brtvsk.geo.repository.GeoRepository
import org.bson.types.ObjectId

class GeoService {

    private val userRep = UserRepository()
    private val geoRepository = GeoRepository()
    private val geoTagsRepository = GeoTagsRepository()

    suspend fun findUser(
        userId:Int) : User? {
        return userRep.findUserById(userId)
    }

    suspend fun createGeo(userId:Int, geoData: GeoDTO.RequestGeo): Geo? {
        val point = Point(coordinates = listOf(geoData.lat,geoData.lng))
        val tags = geoData.tags.toSet()
        return geoRepository.addGeo(userId, point, geoData.type, tags, geoData.raiting, geoData.description)
    }

    suspend fun getGeo(geoId: ObjectId): Geo? {
        return geoRepository.findGeo(geoId)
    }

    suspend fun findGeo(point: Point): Geo? {
        return geoRepository.findGeo(point)
    }

    suspend fun getAllGeos(userId: Int): List<Geo>{
        return geoRepository.getAll(userId)
    }

    suspend fun getAllGeoTags(): Set<GeoTag>{
        return geoTagsRepository.getAll()
    }

    suspend fun visitGeo(userId: Int, geoId: String) : VisitedGeo?{
        val result = geoRepository.incrementVisitedGeo(userId, geoId)
        if(result < 1){
            return geoRepository.addVisitedGeo(userId, geoId)
        }
        return geoRepository.findVisitedGeo(userId, geoId)
    }

    suspend fun getAllVisited(userId: Int) : List<VisitedGeo>{
        return geoRepository.getAllVisited(userId)
    }

    suspend fun visitedToGeos(visited: List<VisitedGeo>) : List<GeoDTO.RespondVisitedGeo>{
        val visitedWithObjectIds = visited.map { ObjectId(it.geoId) to it.counter}
        val geos = geoRepository.findGeosByIds(visitedWithObjectIds.map{it.first})
        return visitedWithObjectIds.map{visited ->
            val geo = geos.find { it._id == visited.first } ?: throw Exception("ObjectId not found")
            val respondGeo = GeoDTO.RespondGeo(geo._id,geo.userId,geo.point.coordinates[0],geo.point.coordinates[1],geo.type.name, geo.tags, geo.raiting, geo.description)
            GeoDTO.RespondVisitedGeo(respondGeo,visited.second)
        }
    }

    suspend fun getVisited(userId: Int, geoId: String) : VisitedGeo?{
        return geoRepository.findVisitedGeo(userId, geoId)
    }

}