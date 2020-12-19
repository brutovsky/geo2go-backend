package com.brtvsk

import com.brtvsk.auth.utils.JwtService
import com.brtvsk.auth.utils.MySession
import com.brtvsk.auth.utils.hash
import com.brtvsk.auth.repository.DatabaseFactory
import com.brtvsk.auth.service.UserService
import com.brtvsk.geo.MongoDB.MongoDB
import com.brtvsk.geo.service.GeoService
import com.brtvsk.routes.geo
import com.brtvsk.routes.users
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.sessions.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.gson.*
import io.ktor.features.*

const val API_VERSION = "/v1"

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(Locations) {}

    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    // Users DB init
    DatabaseFactory.init()
    MongoDB.init()
    val jwtService = JwtService()
    val hashFunction = { s: String -> hash(s) }

    // Geos DB init
    val userService = UserService()
    val geoRepository = GeoService()

    install(Authentication) {
        jwt("jwt") {
            verifier(jwtService.verifier)
            realm = "Geo2Go Server"
            validate {
                val payload = it.payload
                val claim = payload.getClaim("id")
                val claimString = claim.asInt()
                val user = userService.findUserById(claimString)
                user
            }
        }
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    routing {
        get("/") {
            call.respondText("This application should tell you where(geo) to(2) go\nEnjoy", contentType = ContentType.Text.Plain)
        }
        users(userService, jwtService, hashFunction)
        geo(geoRepository)
    }
}