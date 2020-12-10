package com.brtvsk.geo.repository

import com.brtvsk.geo.models.Geo

interface Repository {
    fun addGeo(userId:Int, position: String, description: String): Geo?
    fun findGeo(position: String): Geo?
}