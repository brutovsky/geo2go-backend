package com.brtvsk.avatar.model

import com.brtvsk.auth.models.Avatar
import com.brtvsk.geo.models.GeoTag
import com.brtvsk.geo.models.GeoType
import com.brtvsk.geo.models.Point
import java.io.Serializable

data class AvatarGeoTagProgress(
    val id: Int,
    val avatar: Avatar,
    val name: String,
    val progressGeoTypes: Map<GeoTag, Pair<Int,Int>>,
    val completed: Boolean
) : Serializable

data class AvatarGeoTypeProgress(
    val id: Int,
    val avatar: Avatar,
    val name: String,
    val progressGeoTypes: Map<GeoType, Pair<Int,Int>>,
    val completed: Boolean
) : Serializable

data class AvatarPointProgress(
    val id: Int,
    val avatar: Avatar,
    val name: String,
    val progressGeoTypes: Map<Point, Pair<Int,Int>>,
    val completed: Boolean
) : Serializable