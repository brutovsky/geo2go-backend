package com.brtvsk.routes

import com.brtvsk.API_VERSION
import com.brtvsk.auth.models.User
import com.brtvsk.auth.utils.MySession
import com.brtvsk.avatar.handler.AvatarProgressHandler
import com.brtvsk.geo.dto.GeoDTO
import com.brtvsk.geo.models.GeoType
import com.brtvsk.geo.models.Point
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
import org.bson.types.ObjectId

const val GEO = "$API_VERSION/geo"
const val GEO_GET = "$GEO/get"
const val GEO_ALL = "$GEO/all"
const val GEO_CREATE = "$GEO/create"
const val GEO_CHECK_IN = "$GEO/checkin"
const val GEO_ALL_VISITED_GEOS = "$GEO_ALL/visited"
const val GEO_VISITED_GEOS = "$GEO/visited"

const val GEO_ALL_TYPES = "$GEO/types"
const val GEO_ALL_TAGS = "$GEO/tags"

const val GEO_RECOMMENDATIONS = "$GEO/recommend"

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
@Location(GEO_CHECK_IN)
class GeoCheckInRoute

@KtorExperimentalLocationsAPI
@Location(GEO_ALL_VISITED_GEOS)
class GeoAllVisitedRoute

@KtorExperimentalLocationsAPI
@Location(GEO_VISITED_GEOS)
class GeoVisitedRoute

@KtorExperimentalLocationsAPI
@Location(GEO_ALL_TYPES)
class GeoAllGeoTypesRoute

@KtorExperimentalLocationsAPI
@Location(GEO_ALL_TAGS)
class GeoAllGeoTagsRoute

@KtorExperimentalLocationsAPI
@Location(GEO_RECOMMENDATIONS)
class GeoGetRecommendedRoute


@KtorExperimentalLocationsAPI
fun Route.geo(
    geoService: GeoService,
    avatarProgressHandler: AvatarProgressHandler
) {
    authenticate("jwt") {

        get<GeoGetRecommendedRoute>{
            val user = call.sessions.get<MySession>()?.let { geoService.findUser(it.userId) }
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
                return@get
            }
            try {
                val geoType: String = call.request.queryParameters["geoType"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest, "Missing query parameters :/"
                )
                val lat: String = call.request.queryParameters["lat"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest, "Missing query parameters :/"
                )
                val lng: String = call.request.queryParameters["lng"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest, "Missing query parameters :/"
                )
                val minDistance: String = call.request.queryParameters["lng"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest, "Missing query parameters :/"
                )
                val maxDistance: String = call.request.queryParameters["lng"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest, "Missing query parameters :/"
                )
                val geos = geoService.getRecommendedGeos(user.userId,GeoType.valueOf(geoType), Point(coordinates = listOf(lat.toDouble(),lng.toDouble())),minDistance.toInt()..maxDistance.toInt())
                call.respond(geos)
            } catch (e: Throwable) {
                application.log.error("Failed to get Geos", e)
                call.respond(HttpStatusCode.BadRequest, "Problems getting Geos")
            }
        }

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

        get<GeoAllVisitedRoute>{
            val user = call.sessions.get<MySession>()?.let { geoService.findUser(it.userId) }
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
                return@get
            }
            try {
                val geos = geoService.getAllVisited(user.userId)
                call.respond(geoService.visitedToGeos(geos))
            } catch (e: Throwable) {
                application.log.error("Failed to get Geos", e)
                call.respond(HttpStatusCode.BadRequest, "Problems getting Geos")
            }
        }

        get<GeoVisitedRoute>{
            val user = call.sessions.get<MySession>()?.let { geoService.findUser(it.userId) }
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
                return@get
            }
            try {
                val geoId: String = call.request.queryParameters["geoId"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest, "Missing query parameters :/"
                )
                val geo = geoService.getVisited(user.userId, geoId) ?: return@get call.respond(
                    HttpStatusCode.BadRequest, "Problems getting visited geo :/"
                )
                call.respond(geo)
            } catch (e: Throwable) {
                application.log.error("Failed to get VisitedGeo", e)
                call.respond(HttpStatusCode.BadRequest, "Problems getting VisitedGeo")
            }
        }

        post<GeoCheckInRoute> {
            val user = context.principal<User>()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")

            val geoId = call.receive<ObjectId>()

            val visitedGeo = geoService.visitGeo(user.userId, geoId.toHexString())?.let{
                avatarProgressHandler.handleCheckIn(user.userId,it)
            }

            val geo = geoService.getGeo(geoId)
            geo?.let {
                call.respond(HttpStatusCode.OK)
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