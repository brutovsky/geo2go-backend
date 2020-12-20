package com.brtvsk.geo.models

import org.bson.types.ObjectId

data class Point(
    val type:String = "Point",
    val coordinates:List<Double>
)

data class Geo(
    val _id: ObjectId,
    val userId: Int,
    val point:Point,
    val type: GeoType,
    val tags: Set<Int>,
    val raiting: Map<Integer, Integer>,
    val description: String,
)
