package com.brtvsk.content

import io.ktor.auth.Principal
import java.io.Serializable

data class RequestUser(
    val email: String,
    val displayName: String,
    val password: String,
) : Serializable, Principal

data class RespondUser(
        val email: String,
        val displayName: String,
        val userId: Int?,
) : Serializable, Principal