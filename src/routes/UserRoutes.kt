package com.brtvsk.routes

import com.brtvsk.API_VERSION
import com.brtvsk.auth.JwtService
import com.brtvsk.auth.MySession
import com.brtvsk.content.RequestUser
import com.brtvsk.content.RespondUser
import com.brtvsk.models.User
import com.brtvsk.repository.Repository
import com.brtvsk.repository.Users
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
import io.ktor.response.respondText
import io.ktor.routing.*
import io.ktor.sessions.sessions
import io.ktor.sessions.set
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.postgresql.util.PSQLException
import org.postgresql.util.PSQLState
import org.postgresql.util.PSQLWarning
import java.sql.SQLIntegrityConstraintViolationException
import kotlin.math.log
import kotlin.math.sign

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
                call.respond(HttpStatusCode.Created, RespondUser(
                    email = newUser.email,
                    displayName = newUser.displayName,
                    userId = newUser.userId,
                ))
            }
        } catch (e: ExposedSQLException){
            application.log.error("Failed to register user", e)
            when(e.sqlState){
                "23505" -> call.respond(HttpStatusCode.BadRequest, "Such user already exists :/")
                else    -> call.respond(HttpStatusCode.BadRequest, "Problems creating User :/")
            }
        } catch (e: Throwable) {
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems creating User :/")
        }
    }

    //TODO: make it work with json RequestUser
    post<UserLoginRoute> {
        val signinParameters = call.receive<Parameters>()
        val password = signinParameters["password"]
            ?: return@post call.respond(
                HttpStatusCode.Unauthorized, "Missing Fields")
        val email = signinParameters["email"]
            ?: return@post call.respond(
                HttpStatusCode.Unauthorized, "Missing Fields")
        val hash = hashFunction(password)
        try {
            val currentUser = db.findUserByEmail(email)
            currentUser?.userId?.let {
                if (currentUser.passwordHash == hash) {
                    call.sessions.set(MySession(it))
                    call.respond(jwtService.generateToken(currentUser))
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
                }
            }
        }catch (e: Throwable){
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
        }
    }
}