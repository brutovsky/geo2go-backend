package com.brtvsk.auth.repository

import com.brtvsk.auth.models.User
import com.brtvsk.auth.repository.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.update

class UserRepository: Repository {

    override suspend fun addUser(
        email: String,
        displayName: String,
        avatar: String,
        passwordHash: String) : User? {
        var statement : InsertStatement<Number>? = null
        dbQuery {
            statement = Users.insert { user ->
                user[Users.email] = email
                user[Users.displayName] = displayName
                user[Users.avatar] = avatar
                user[Users.passwordHash] = passwordHash
            }
        }
        return rowToUser(statement?.resultedValues?.get(0))
    }

    override suspend fun findUser(userId: Int) = dbQuery {
        Users.select { Users.userId.eq(userId) }
            .map { rowToUser(it) }.singleOrNull()
    }

    override suspend fun findUserByEmail(email: String)= dbQuery {
        Users.select { Users.email.eq(email) }
            .map { rowToUser(it) }.singleOrNull()
    }

    override suspend fun setUsername(userId:Int, username: String) : Int? {
        var result:Int? = null
        dbQuery {
            result = Users.update({ Users.userId eq userId}) {
                it[Users.displayName] = username
            }
        }
        return result
    }

    override suspend fun setAvatar(userId:Int, avatar: String) : Int? {
        var result:Int? = null
        dbQuery {
            result = Users.update({ Users.userId eq userId}) {
                it[Users.avatar] = avatar
            }
        }
        return result
    }

    override suspend fun addAvatar(userId:Int, avatar: String) : String? {
        var statement : InsertStatement<Number>? = null
        dbQuery {
            statement = Avatars.insert{
                it[Avatars.userId] = userId
                it[Avatars.avatar] = avatar
            }
        }
        return rowToAvatar(statement?.resultedValues?.get(0))
    }

    override suspend fun getAvatars(userId: Int) = dbQuery{
        Avatars.select{ Avatars.userId.eq(userId) }
            .mapNotNull { rowToAvatar(it) }
    }

    private fun rowToUser(row: ResultRow?): User? {
        if (row == null) {
            return null
        }
        return User(
            userId = row[Users.userId],
            email = row[Users.email],
            displayName = row[Users.displayName],
            avatar = row[Users.avatar],
            passwordHash = row[Users.passwordHash]
        )
    }

    private fun rowToAvatar(row: ResultRow?): String? = if (row == null) null else row[Avatars.avatar]

}
