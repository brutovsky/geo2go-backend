package com.brtvsk.auth.repository

import com.brtvsk.auth.models.User

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
}