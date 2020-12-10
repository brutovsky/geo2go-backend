package com.brtvsk.auth.dto

import io.ktor.auth.Principal
import java.io.Serializable

object UserDTO {

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

    data class LoginRespond(
        val user: RespondUser,
        val jwtToken: String
    ) : Serializable, Principal

}

