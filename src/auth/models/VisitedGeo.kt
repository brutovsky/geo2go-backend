package com.brtvsk.auth.models

import io.ktor.auth.*
import java.io.Serializable

data class VisitedGeo(
    val userId: Int,
    val geoId: String,
    val counter: Int
) : Serializable, Principal
