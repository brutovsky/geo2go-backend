package com.brtvsk.avatar.model

import com.brtvsk.auth.models.Avatar
import com.brtvsk.geo.models.GeoTag
import com.brtvsk.geo.models.GeoType
import com.brtvsk.geo.models.Point
import java.io.Serializable

data class AvatarProgress(
    val avatarId: Int,
    val userId: Int,
    val avatar: Avatar,
    val counter: Int,
) : Serializable
