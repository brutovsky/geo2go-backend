package com.brtvsk.routes

import com.brtvsk.API_VERSION
import com.brtvsk.auth.dto.UserDTO
import com.brtvsk.auth.utils.JwtService
import com.brtvsk.auth.utils.MySession
import com.brtvsk.auth.dto.UserDTO.RequestUser
import com.brtvsk.auth.dto.UserDTO.RespondUser
import com.brtvsk.auth.models.Avatar
import com.brtvsk.auth.repository.Repository
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.routing.get
import io.ktor.sessions.*
import org.jetbrains.exposed.exceptions.ExposedSQLException

const val USERS = "$API_VERSION/users"
const val USER_LOGIN = "$USERS/login"
const val USER_CREATE = "$USERS/create"
const val USER_AVATARS = "$USERS/avatars"
const val USER_SET_AVATAR = "$USERS/avatar"
const val USER_SET_USERNAME = "$USERS/username"

@KtorExperimentalLocationsAPI
@Location(USER_LOGIN)
class UserLoginRoute

@KtorExperimentalLocationsAPI
@Location(USER_CREATE)
class UserCreateRoute

@KtorExperimentalLocationsAPI
@Location(USER_AVATARS)
class UserAvatarsRoute

@KtorExperimentalLocationsAPI
@Location(USER_SET_AVATAR)
class UserSetAvatarRoute

@KtorExperimentalLocationsAPI
@Location(USER_SET_USERNAME)
class UserSetUsernameRoute

@KtorExperimentalLocationsAPI
fun Route.users(
    db: Repository,
    jwtService: JwtService,
    hashFunction: (String) -> String
) {
    post<UserCreateRoute> {
        val userData = call.receive<RequestUser>()
        application.log.info("Requested user to create: ", userData)
        val hash = hashFunction(userData.password)
        try {
            val newUser = db.addUser(userData.email, userData.displayName, Avatar.Pepe.name, hash)
            newUser?.userId?.let {
                call.sessions.set(MySession(it))
                db.addAvatar(newUser.userId, Avatar.Pepe.name)
                db.addAvatar(newUser.userId, Avatar.Cheems.name)
                db.addAvatar(newUser.userId, Avatar.Geco.name)
                db.addAvatar(newUser.userId, Avatar.Default.name)
                val respondUser = RespondUser(
                    email = newUser.email,
                    displayName = newUser.displayName,
                    avatar = newUser.avatar,
                    userId = newUser.userId,
                )
                val token = jwtService.generateToken(newUser)
                call.respond(
                    HttpStatusCode.Created, UserDTO.LoginRespond(
                        user = respondUser,
                        jwtToken = token,
                    )
                )
            }
        } catch (e: ExposedSQLException) {
            application.log.error("Failed to register user", e)
            when (e.sqlState) {
                "23505" -> call.respond(HttpStatusCode.BadRequest, "Such user already exists :/")
                else -> call.respond(HttpStatusCode.BadRequest, "Problems creating User :/")
            }
        } catch (e: Throwable) {
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems creating User :/")
        }
    }

    post<UserLoginRoute> {
        val userData = call.receive<RequestUser>()
        application.log.info("Requested user to login: ", userData)
        val hash = hashFunction(userData.password)
        try {
            val currentUser = db.findUserByEmail(userData.email)
            currentUser?.userId?.let {
                if (currentUser.passwordHash == hash) {
                    call.sessions.set(MySession(it))
                    val respondUser = RespondUser(
                        email = currentUser.email,
                        displayName = currentUser.displayName,
                        avatar = currentUser.avatar,
                        userId = currentUser.userId,
                    )
                    val token = jwtService.generateToken(currentUser)
                    call.respond(
                        HttpStatusCode.Created, UserDTO.LoginRespond(
                            user = respondUser,
                            jwtToken = token,
                        )
                    )
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
                }
            }
        } catch (e: Throwable) {
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
        }
    }

    authenticate("jwt") {
        get<UserAvatarsRoute> {
            val user = call.sessions.get<MySession>()?.let { db.findUser(it.userId) }
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
                return@get
            }
            try {
                val avatars = db.getAvatars(user.userId)
                call.respond(avatars)
            } catch (e: Throwable) {
                application.log.error("Failed to get Avatars", e)
                call.respond(HttpStatusCode.BadRequest, "Problems getting Avatars")
            }
        }
        post<UserSetAvatarRoute> {
            val params = call.receive<Parameters>()
            val avatar = params["avatar"]
                ?: return@post call.respond(
                    HttpStatusCode.BadRequest, "Missing Fields"
                )
            val user = call.sessions.get<MySession>()?.let { db.findUser(it.userId) }
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
            try {
                db.setAvatar(user.userId, avatar)
                call.respond(HttpStatusCode.OK)
            } catch (e: Throwable) {
                application.log.error("Failed to set Avatar", e)
                call.respond(HttpStatusCode.BadRequest, "Problems setting Avatar")
            }
        }
        post<UserSetUsernameRoute>{
            val params = call.receive<Parameters>()
            val avatar = params["username"]
                ?: return@post call.respond(
                    HttpStatusCode.BadRequest, "Missing Fields"
                )
            val user = call.sessions.get<MySession>()?.let { db.findUser(it.userId) }
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
            try {
                db.setUsername(user.userId, avatar)
                call.respond(HttpStatusCode.OK)
            } catch (e: Throwable) {
                application.log.error("Failed to set Username", e)
                call.respond(HttpStatusCode.BadRequest, "Problems setting Username")
            }
        }
    }

}