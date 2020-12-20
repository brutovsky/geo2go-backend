package com.brtvsk.auth.repository

import com.brtvsk.auth.models.User
import com.brtvsk.auth.repository.DatabaseFactory.dbQuery
import com.brtvsk.auth.repository.tables.*
import com.brtvsk.auth.utils.*
import com.brtvsk.avatar.model.AvatarCondition
import com.brtvsk.auth.repository.tables.AvatarProgress
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNotNull
import org.jetbrains.exposed.sql.statements.InsertStatement
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
            statement = UserAvatars.insert {
                it[UserAvatars.userId] = userId
                it[UserAvatars.avatar] = avatar
            }
        }
        return (statement?.resultedValues?.get(0)).toAvatar()
    }

    override suspend fun getAvatars(userId: Int) = dbQuery {
        UserAvatars.select { UserAvatars.userId.eq(userId) }
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

    override suspend fun updateAvatarProgress(userId: Int, tagIds: List<Int>) {
        transaction {
            TransactionManager.current().exec(
                """WITH handled_avatars AS (WITH received_avatars AS (SELECT avatar
                                                   FROM user_avatars
                                                   WHERE user_id = $userId)
INSERT
INTO avatar_progress AS progress(user_id, avatar, counter)
  SELECT
    $userId,
    avatar_condition.avatar,
    1
  FROM avatar_conditions avatar_condition
  WHERE avatar_condition.avatar != ALL (SELECT *
                                        FROM received_avatars) AND avatar_condition.tag_id = ANY (${
                    tagIds.joinToString(prefix = "ARRAY[",
                        postfix = "]")
                })
ON CONFLICT (user_id, avatar)
  DO UPDATE SET
    counter = progress.counter + 1
RETURNING progress.avatar, progress.counter)
INSERT INTO user_avatars (user_id, avatar)
  SELECT
    $userId,
    handled_avatar.avatar
  FROM handled_avatars handled_avatar
    INNER JOIN avatar_conditions avatar_condition ON handled_avatar.avatar = avatar_condition.avatar
  WHERE handled_avatar.counter = avatar_condition.target;
            """
            )
        }
    }

    /*dbQuery {
    val userAvatars = UserAvatars.select { UserAvatars.userId eq userId }
    //val userAvatars = com.brtvsk.auth.repository.tables.AvatarProgress. { UserAvatars.userId eq userId }

    val t = AvatarConditions.slice().selectAll()

    //select avatarId from avatarConditions where avatarId != ALL(Select avatar id from userAvatar where userId == userID)

    val complexJoin = Join(
        AvatarConditions, UserAvatars,
        onColumn = AvatarConditions.avatar, otherColumn = UserAvatars.avatar,
        joinType = JoinType.LEFT,
        additionalConstraint = { UserAvatars.userId.isNull() })
    val res = complexJoin.slice(GeoTags.id, GeoTags.name, GeoTags.type, UserTags.tagId.isNotNull())
    res.selectAll().mapNotNull { it.toFavTag() }

    listOf<com.brtvsk.avatar.model.AvatarProgress>()
}*/

    override suspend fun getAvatarConditions(avatars: List<String>) = dbQuery {
        val complexJoin = Join(
            AvatarConditions, GeoTags,
            onColumn = AvatarConditions.tagId, otherColumn = GeoTags.id,
            joinType = JoinType.INNER)
        val res = complexJoin.slice(AvatarConditions.avatar, AvatarConditions.target, GeoTags.name, GeoTags.type)
        res.select { AvatarConditions.avatar inList avatars }.mapNotNull { it.toAvatarCondition() }
    }

    override suspend fun completeAvatarProgress(userId: Int, avatar: String) = dbQuery {
        UserAvatars.insert {
            it[UserAvatars.userId] = userId
            it[UserAvatars.avatar] = avatar
        }
        AvatarProgress.deleteWhere { (AvatarProgress.userId eq userId) and (AvatarProgress.avatar eq avatar) }
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
