package com.brtvsk.auth.models

import io.ktor.auth.Principal
import java.io.Serializable

data class User(
    val userId: Int,
    val email: String,
    val displayName: String,
    val avatar: String,
    val passwordHash: String
) : Serializable, Principal
