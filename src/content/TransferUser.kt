package com.brtvsk.content

import io.ktor.auth.Principal
import java.io.Serializable

data class TransferUser(
        val userId: Int?,
        val email: String,
        val displayName: String
) : Serializable, Principal