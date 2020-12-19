package com.brtvsk.geo.dto

import com.brtvsk.geo.models.GeoType
import org.bson.types.ObjectId

object GeoDTO {

    data class RequestGeo(
        val lat: Double,
        val lng: Double,
        val type: GeoType,
        val tags: List<String>,
        val raiting: Map<Integer,Integer>,
        val description: String,
    )

    data class RespondGeo(
        val id: ObjectId,
        val userId: Int,
        val lat: Double,
        val lng: Double,
        val type: String,
        val tags: Set<String>,
        val raiting: Map<Integer,Integer>,
        val description: String,
    )

}