package com.brtvsk.auth.service

import com.brtvsk.auth.models.User
import com.brtvsk.auth.models.VisitedGeo
import com.brtvsk.auth.repository.UserRepository
import com.brtvsk.geo.models.FavTag
import com.brtvsk.geo.models.GeoTag
import com.brtvsk.geo.models.UserFavTag
import io.ktor.application.*

class UserService {

    private val userRep = UserRepository()

    suspend fun createUser(
        email: String,
        displayName: String,
        avatar: String,
        passwordHash: String) : User? {
        return userRep.addUser(email,displayName,avatar,passwordHash)
    }

    suspend fun findUserById(
        userId:Int) : User? {
        return userRep.findUserById(userId)
    }

    suspend fun findUserByEmail(email: String) : User? {
        return userRep.findUserByEmail(email)
    }

    suspend fun setUsername(userId:Int, username: String) : Int? {
        return userRep.updateUser(User(userId = userId, displayName = username))
    }

    suspend fun setAvatar(userId:Int, avatar: String) : Int? {
        return userRep.updateUser(User(userId = userId, avatar = avatar))
    }

    suspend fun addAvatar(userId:Int, avatar: String) : String? {
        return userRep.addAvatar(userId, avatar)
    }

    suspend fun getAvatars(userId: Int) : Set<String>{
        return userRep.getAvatars(userId).toSet()
    }

    suspend fun setFavTags(userId: Int, tagsIds: List<Int>) : List<UserFavTag>{
        return userRep.setFavTags(userId, tagsIds)
    }

    suspend fun getFavTags(userId: Int) : List<FavTag>{
        return userRep.getFavTags(userId)
    }

}