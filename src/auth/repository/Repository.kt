package com.brtvsk.auth.repository

import com.brtvsk.auth.models.User

interface Repository {
    suspend fun addUser(email: String,
                        displayName: String,
                        avatar: String,
                        passwordHash: String): User?
    suspend fun findUser(userId: Int): User?
    suspend fun findUserByEmail(email: String): User?
    suspend fun setAvatar(userId: Int, avatar:String): Int?
    suspend fun addAvatar(userId: Int, avatar:String): String?
    suspend fun getAvatars(userId: Int): List<String>
}