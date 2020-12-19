package com.brtvsk.geo.models

data class GeoTag(
    val tag:String,
    val type:GeoType? = null,
    val id:Int? = null,
)

val geoTags:List<GeoTag> = listOf(
    GeoTag("spicy",GeoType.Cafe),
    GeoTag("chinese",GeoType.Cafe),
    GeoTag("ukrainian",GeoType.Cafe),
    GeoTag("cool"),
    GeoTag("not good"),
)