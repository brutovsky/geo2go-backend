package com.brtvsk.avatar.model

import com.brtvsk.auth.models.Avatar
import com.brtvsk.geo.models.GeoTag
import com.brtvsk.geo.models.GeoType
import com.brtvsk.geo.models.geoTags
import java.io.Serializable

data class AvatarCondition(
    val avatar: Avatar,
    val target: Int,
    val condition: GeoTag,
) : Serializable

val avatarConditions:List<AvatarCondition> = listOf(
    AvatarCondition(Avatar.MarioGuy, 4, geoTags.first { it.tag == "sadistic" }),
    AvatarCondition(Avatar.Stronk, 2, geoTags.first { it.tag == "motivating" }),
    AvatarCondition(Avatar.CoolPepe, 1, geoTags.first { it.tag == "cool" }),
)