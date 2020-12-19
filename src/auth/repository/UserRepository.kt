package com.brtvsk.auth.repository

import com.brtvsk.auth.models.User
import com.brtvsk.auth.models.VisitedGeo
import com.brtvsk.auth.repository.DatabaseFactory.dbQuery
import com.brtvsk.auth.repository.tables.Avatars
import com.brtvsk.auth.repository.tables.Users
import com.brtvsk.auth.repository.tables.VisitedGeos
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.InsertStatement

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

    override suspend fun findUserById(userId: Int) = dbQuery {
        Users.select { Users.userId.eq(userId) }
            .map { rowToUser(it) }.singleOrNull()
    }

    override suspend fun findUserByEmail(email: String) = dbQuery {
        Users.select { Users.email.eq(email) }
            .map { rowToUser(it) }.singleOrNull()
    }

    override suspend fun updateUser(user:User) : Int? {
        var result:Int? = null
        dbQuery {
            result = Users.update({ Users.userId eq user.userId}) {
                if(user.email != null)it[Users.email] = user.email
                if(user.displayName != null)it[Users.displayName] = user.displayName
                if(user.avatar != null)it[Users.avatar] = user.avatar
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
