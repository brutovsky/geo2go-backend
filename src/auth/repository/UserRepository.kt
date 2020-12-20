package com.brtvsk.auth.repository

import com.brtvsk.auth.models.User
import com.brtvsk.auth.models.VisitedGeo
import com.brtvsk.auth.repository.DatabaseFactory.dbQuery
import com.brtvsk.auth.repository.tables.*
import com.brtvsk.auth.utils.toAvatar
import com.brtvsk.auth.utils.toFavTag
import com.brtvsk.auth.utils.toUser
import com.brtvsk.auth.utils.toUserFavTag
import com.brtvsk.geo.models.FavTag
import com.brtvsk.geo.models.GeoTag
import com.brtvsk.geo.models.GeoType
import com.brtvsk.geo.models.UserFavTag
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNotNull
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.statements.Statement
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository : Repository {

    override suspend fun addUser(
        email: String,
        displayName: String,
        avatar: String,
        passwordHash: String,
    ): User? {
        var statement: InsertStatement<Number>? = null
        dbQuery {
            statement = Users.insert { user ->
                user[Users.email] = email
                user[Users.displayName] = displayName
                user[Users.avatar] = avatar
                user[Users.passwordHash] = passwordHash
            }
        }
        return (statement?.resultedValues?.get(0)).toUser()
    }

    override suspend fun findUserById(userId: Int) = dbQuery {
        Users.select { Users.userId.eq(userId) }
            .map { it.toUser() }.singleOrNull()
    }

    override suspend fun findUserByEmail(email: String) = dbQuery {
        Users.select { Users.email.eq(email) }
            .map { it.toUser() }.singleOrNull()
    }

    override suspend fun updateUser(user: User): Int? {
        var result: Int? = null
        dbQuery {
            result = Users.update({ Users.userId eq user.userId }) {
                if (user.email != null) it[Users.email] = user.email
                if (user.displayName != null) it[Users.displayName] = user.displayName
                if (user.avatar != null) it[Users.avatar] = user.avatar
            }
        }
        return result
    }

    override suspend fun addAvatar(userId: Int, avatar: String): String? {
        var statement: InsertStatement<Number>? = null
        dbQuery {
            statement = Avatars.insert {
                it[Avatars.userId] = userId
                it[Avatars.avatar] = avatar
            }
        }
        return (statement?.resultedValues?.get(0)).toAvatar()
    }

    override suspend fun getAvatars(userId: Int) = dbQuery {
        Avatars.select { Avatars.userId.eq(userId) }
            .mapNotNull { it.toAvatar() }
    }

    override suspend fun setFavTags(userId: Int, tagsIds: List<Int>) = dbQuery {
        UserTags.deleteWhere { UserTags.userId eq userId }
        UserTags.batchInsert(tagsIds) { id ->
            this[UserTags.userId] = userId
            this[UserTags.tagId] = id
        }.mapNotNull { it.toUserFavTag() }
    }

    override suspend fun getFavTags(userId: Int) = dbQuery {
        val complexJoin = Join(
            GeoTags, UserTags,
            onColumn = GeoTags.id, otherColumn = UserTags.tagId,
            joinType = JoinType.LEFT,
            additionalConstraint = { UserTags.userId eq userId })
        val res = complexJoin.slice(GeoTags.id, GeoTags.name, GeoTags.type, UserTags.tagId.isNotNull())
        res.selectAll().mapNotNull { it.toFavTag() }
    }

}

/*var statement = null
val t = transaction {
val conn = TransactionManager.current().connection
val query = "REFRESH MATERIALIZED VIEW someview"
val statement = conn.prepareStatement(query, true)
statement.executeQuery()
}*/

/*
TransactionManager.current().exec(
    "select * from nodes " +
    "where date_part('microseconds', updated_at - last_deployed) > 1000 " +
    "order by last_change_status asc " +
    "limit " + max + " offset " + offset

 */
