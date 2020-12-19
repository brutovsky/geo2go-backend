package com.brtvsk.geo.repository

import com.brtvsk.geo.models.Geo
import com.brtvsk.geo.models.GeoType
import com.brtvsk.geo.models.Point

interface Repository {
    suspend fun addGeo(userId:Int, point: Point,
                       type: GeoType, tags: Set<String>,
                       raiting: Map<Integer, Integer>, description: String): Geo?
    suspend fun findGeo(point:Point): Geo?
    suspend fun getAll(userId: Int): List<Geo>
}