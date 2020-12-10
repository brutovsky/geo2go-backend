package com.brtvsk.routes

import com.brtvsk.API_VERSION
import com.brtvsk.auth.dto.UserDTO
import com.brtvsk.auth.utils.JwtService
import com.brtvsk.auth.utils.MySession
import com.brtvsk.auth.dto.UserDTO.RequestUser
import com.brtvsk.auth.dto.UserDTO.RespondUser
import com.brtvsk.repository.Repository
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.post
import io.ktor.request.*
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import org.jetbrains.exposed.exceptions.ExposedSQLException

const val USERS = "$API_VERSION/users"
const val USER_LOGIN = "$USERS/login"
const val USER_CREATE = "$USERS/create"

@KtorExperimentalLocationsAPI
@Location(USER_LOGIN)
class UserLoginRoute

@KtorExperimentalLocationsAPI
@Location(USER_CREATE)
class UserCreateRoute

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
            val newUser = db.addUser(userData.email, userData.displayName, hash)
            newUser?.userId?.let {
                call.sessions.set(MySession(it))
                val respondUser = RespondUser(
                    email = newUser.email,
                    displayName = newUser.displayName,
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
}