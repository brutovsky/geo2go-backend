package com.brtvsk.geo.service

import com.brtvsk.auth.models.User
import com.brtvsk.auth.repository.UserRepository
import com.brtvsk.geo.dto.GeoDTO
import com.brtvsk.geo.models.Geo
import com.brtvsk.geo.models.GeoTag
import com.brtvsk.geo.models.Point
import com.brtvsk.geo.repository.GeoRepository
import com.brtvsk.geo.repository.GeoTagsRepository

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

    suspend fun findGeo(point: Point): Geo? {
        return geoRepository.findGeo(point)
    }

    suspend fun getAllGeos(userId: Int): List<Geo>{
        return geoRepository.getAll(userId)
    }

    suspend fun getAllGeoTags(): Set<GeoTag>{
        return geoTagsRepository.getAll()
    }

}