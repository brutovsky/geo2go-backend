package com.brtvsk.auth.models

import io.ktor.auth.Principal
import java.io.Serializable

data class User(
    val userId: Int,
    val email: String? = null,
    val displayName: String? = null,
    val avatar: String? = null,
    val passwordHash: String? = null
) : Serializable, Principal
