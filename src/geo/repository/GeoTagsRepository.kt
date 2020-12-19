package com.brtvsk.geo.repository

import com.brtvsk.geo.MongoDB.MongoDB
import com.brtvsk.geo.models.Geo
import com.brtvsk.geo.models.GeoTag
import com.brtvsk.geo.models.GeoType
import com.brtvsk.geo.models.Point
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.litote.kmongo.id.ObjectIdGenerator

class GeoTagsRepository{

    private val geo = MongoDB.mongoDB.getCollection<GeoTag>(Geos.COL_GEO_TAGS)

    fun getAll(): Set<GeoTag> {
        val geoTags = geo.find()
        return geoTags.toSet()
    }

}