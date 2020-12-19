package com.brtvsk.routes

import com.brtvsk.API_VERSION
import com.brtvsk.auth.models.User
import com.brtvsk.auth.utils.MySession
import com.brtvsk.geo.dto.GeoDTO
import com.brtvsk.geo.models.GeoType
import com.brtvsk.geo.repository.Repository
import com.brtvsk.geo.service.GeoService
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

const val GEO_ALL_GEOTYPES = "$GEO/geotypes"
const val GEO_ALL_GEOTAGS = "$GEO/geotags"

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
@Location(GEO_ALL_GEOTYPES)
class GeoAllGeoTypesRoute

@KtorExperimentalLocationsAPI
@Location(GEO_ALL_GEOTAGS)
class GeoAllGeoTagsRoute

@KtorExperimentalLocationsAPI
fun Route.geo(
    geoService: GeoService
) {
    authenticate("jwt") {

        get<GeoAllRoutes>{
            val user = call.sessions.get<MySession>()?.let { geoService.findUser(it.userId) }
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
                return@get
            }
            try {
                val geos = geoService.getAllGeos(user.userId)
                call.respond(geos)
            } catch (e: Throwable) {
                application.log.error("Failed to get Geos", e)
                call.respond(HttpStatusCode.BadRequest, "Problems getting Geos")
            }
        }

        post<GeoCreateRoute> {
            val user = context.principal<User>()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")

            val geoData = call.receive<GeoDTO.RequestGeo>()
            application.log.info("Requested geo to create: $geoData")

            if(geoData.type == null){
                return@post call.respond(HttpStatusCode.BadRequest, "Bad GeoType :/")
            }

            val newGeo = geoService.createGeo(user.userId, geoData)
            newGeo?.let {
                call.respond(
                    HttpStatusCode.Created, GeoDTO.RespondGeo(
                        id = newGeo._id,
                        userId = newGeo.userId,
                        lat = newGeo.point.coordinates[0],
                        lng = newGeo.point.coordinates[1],
                        type = newGeo.type.name,
                        tags = newGeo.tags,
                        raiting = newGeo.raiting,
                        description = newGeo.description
                    )
                )
            }
        }

    }

    get<GeoAllGeoTypesRoute>{
        try {
            call.respond(GeoType.values())
        } catch (e: Throwable) {
            application.log.error("Failed to get GeoTypes", e)
            call.respond(HttpStatusCode.BadRequest, "Problems getting GeoTypes")
        }
    }

    get<GeoAllGeoTagsRoute>{
        try {
            call.respond(geoService.getAllGeoTags())
        } catch (e: Throwable) {
            application.log.error("Failed to get GeoTags", e)
            call.respond(HttpStatusCode.BadRequest, "Problems getting GeoTags")
        }
    }

}