package com.brtvsk.geo.models

data class GeoTag(
    val tag:String,
    val type:GeoType? = null,
    val id:Int? = null,
)

val geoTags:List<GeoTag> = listOf(
    GeoTag("spicy",GeoType.Food),
    GeoTag("chinese",GeoType.Food),
    GeoTag("ukrainian",GeoType.Food),
    GeoTag("funny",GeoType.Entertainment),
    GeoTag("sadistic",GeoType.Entertainment),
    GeoTag("relax",GeoType.Nature),
    GeoTag("motivating",GeoType.Sports),
    GeoTag("cheap",GeoType.Store),
    GeoTag("cool"),
    GeoTag("not good"),
    GeoTag("bullshit"),
)