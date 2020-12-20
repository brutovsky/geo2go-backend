package com.brtvsk.auth.repository

import com.brtvsk.auth.models.User
import com.brtvsk.avatar.model.AvatarCondition
import com.brtvsk.avatar.model.AvatarProgress
import com.brtvsk.geo.models.FavTag
import com.brtvsk.geo.models.UserFavTag

interface Repository {
    suspend fun addUser(email: String,
                        displayName: String,
                        avatar: String,
                        passwordHash: String): User?
    suspend fun findUserById(userId: Int): User?
    suspend fun findUserByEmail(email: String): User?
    suspend fun updateUser(user:User): Int?
    suspend fun addAvatar(userId: Int, avatar:String): String?
    suspend fun getAvatars(userId: Int): List<String>
    suspend fun setFavTags(userId:Int, tagsIds: List<Int>): List<UserFavTag>
    suspend fun getFavTags(userId:Int): List<FavTag>
    suspend fun updateAvatarProgress(userId:Int, tagIds:List<Int>)
    suspend fun getAvatarConditions(avatars:List<String>): List<AvatarCondition>
    suspend fun completeAvatarProgress(userId:Int, avatar:String): Int?
}