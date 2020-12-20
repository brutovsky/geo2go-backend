package com.brtvsk.avatar.handler

import com.brtvsk.auth.models.VisitedGeo
import com.brtvsk.auth.repository.UserRepository
import com.brtvsk.geo.repository.GeoRepository
import org.bson.types.ObjectId
import com.brtvsk.avatar.model.AvatarProgress

class AvatarProgressHandler() {

    private val userRep = UserRepository()
    private val geoRepository = GeoRepository()

    suspend fun handleCheckIn(userId:Int, visitedGeo: VisitedGeo){
        geoRepository.findGeo(ObjectId(visitedGeo.geoId))?.let {
            userRep.updateAvatarProgress(userId, it.tags.toList())
        }
        /*val avatarProgress: List<AvatarProgress> = geoRepository.findGeo(ObjectId(visitedGeo.geoId))?.let {
            userRep.updateAvatarProgress(userId, it.tags.toList())
        }
            ?: throw Exception("SOMETHING WENT WRONG !!! WTF !!!") //TODO: remove this bullshit

        val avatarConditions = userRep.getAvatarConditions(avatarProgress.map { it.avatar.name })

        avatarProgress.forEach{ prog ->
            val cond = avatarConditions.first { it.avatar.name == prog.avatar.name }
            if(prog.counter >= cond.target){
                userRep.completeAvatarProgress(prog.userId, prog.avatar.name)
            }
        }
*/
    }

}