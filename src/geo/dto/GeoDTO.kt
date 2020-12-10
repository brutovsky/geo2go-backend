package com.brtvsk.geo.dto

import org.bson.types.ObjectId

object GeoDTO {

    data class RequestGeo(
        val userId: Int,
        val position: String,
        val description: String,
    )

    data class RespondGeo(
        val id: ObjectId,
        val userId: Int,
        val position: String,
        val description: String,
    )

}