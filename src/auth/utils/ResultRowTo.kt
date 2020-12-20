package com.brtvsk.auth.utils

import com.brtvsk.auth.models.Avatar
import com.brtvsk.auth.models.User
import com.brtvsk.auth.repository.tables.*
import com.brtvsk.avatar.model.AvatarCondition
import com.brtvsk.geo.models.FavTag
import com.brtvsk.geo.models.GeoTag
import com.brtvsk.geo.models.GeoType
import com.brtvsk.geo.models.UserFavTag
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNotNull

fun ResultRow?.toUser(): User? = this?.let {
    return User(
        userId = this[Users.userId],
        email = this[Users.email],
        displayName = this[Users.displayName],
        avatar = this[Users.avatar],
        passwordHash = this[Users.passwordHash]
    )
}

fun ResultRow?.toAvatar(): String? = this?.let { this[UserAvatars.avatar] }

fun ResultRow?.toGeoTag(): GeoTag? = this?.let {
    GeoTag(
        id = this[GeoTags.id],
        tag = this[GeoTags.name],
        type = this[GeoTags.type]?.let { GeoType.valueOf(it) },
    )
}

fun ResultRow?.toUserFavTag(): UserFavTag? = this?.let {
    UserFavTag(
        userId = this[UserTags.userId],
        tagId = this[UserTags.tagId],
    )
}

fun ResultRow?.toFavTag(): FavTag? = this?.let {
    return FavTag(
        tagId = this[GeoTags.id],
        tagName = this[GeoTags.name],
        tagType = this[GeoTags.type]?.let { GeoType.valueOf(it) },
        isFav = this[UserTags.tagId.isNotNull()]
    )
}

fun ResultRow?.toAvatarCondition(): AvatarCondition? = this?.let {
    return AvatarCondition(
        avatar = Avatar.valueOf(this[AvatarConditions.avatar]),
        condition = GeoTag(this[GeoTags.name], this[GeoTags.type]?.let { GeoType.valueOf(it) }),
        target = this[AvatarConditions.target]
    )
}