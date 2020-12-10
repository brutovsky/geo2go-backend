package com.brtvsk.geo.repository

import com.brtvsk.geo.models.Geo

interface Repository {
    fun addGeo(position:String): Geo?
    fun findGeo(geoId: Int): Geo?
}