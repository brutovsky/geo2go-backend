package com.brtvsk.routes

import com.brtvsk.API_VERSION
import com.brtvsk.auth.utils.MySession
import com.brtvsk.geo.dto.GeoDTO
import com.brtvsk.geo.repository.Repository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

const val GEO = "$API_VERSION/geo"
const val GEO_GET = "$GEO/get"
const val GEO_ALL = "$GEO/all"
const val GEO_CREATE = "$GEO/create"

@KtorExperimentalLocationsAPI
@Location(GEO_GET)
class GeoGetRoute

@KtorExperimentalLocationsAPI
@Location(GEO_ALL)
class GeoAllRoutes

@KtorExperimentalLocationsAPI
@Location(GEO_CREATE)
class GeoCreateRoute

@KtorExperimentalLocationsAPI
fun Route.geo(
    userRep: com.brtvsk.auth.repository.Repository,
    geoRep: Repository
) {
    authenticate("jwt") {

        get<GeoAllRoutes>{
            val user = call.sessions.get<MySession>()?.let { userRep.findUser(it.userId) }
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
                return@get
            }
            try {
                val geos = geoRep.getAll(user.userId)
                call.respond(geos)
            } catch (e: Throwable) {
                application.log.error("Failed to get Geos", e)
                call.respond(HttpStatusCode.BadRequest, "Problems getting Geos")
            }
        }

        post<GeoCreateRoute> {

            val geoData = call.receive<GeoDTO.RequestGeo>()
            application.log.info("Requested geo to create: ", geoData)

            val newGeo = geoRep.addGeo(geoData.userId, geoData.position, geoData.description)
            newGeo?.let {
                call.respond(
                    HttpStatusCode.Created, GeoDTO.RespondGeo(
                        id = newGeo._id,
                        userId = newGeo.userId,
                        position = newGeo.position,
                        description = newGeo.description
                    )
                )
            }
        }

    }
}