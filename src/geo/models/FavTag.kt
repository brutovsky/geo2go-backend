package com.brtvsk.geo.models

data class FavTag (
    val tagId:Int,
    val tagName:String,
    val tagType:GeoType?,
    val isFav:Boolean,
)