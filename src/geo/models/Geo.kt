package com.brtvsk.geo.models

import org.bson.types.ObjectId

data class Geo(
    val _id: ObjectId,
    val userId: Int,
    val position: String,
    val description: String,
)
