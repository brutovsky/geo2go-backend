package com.brtvsk.auth

import io.ktor.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@KtorExperimentalAPI
val hashKey = hex(System.getenv("SECRET_KEY"))

@KtorExperimentalAPI
val hmacKey = SecretKeySpec(hashKey, "HmacSHA1")

@KtorExperimentalAPI
fun hash(password: String): String{
    val hmac = Mac.getInstance("HmacSHA1")
    hmac.init(hmacKey)
    return hex(hmac.doFinal(password.toByteArray(Charsets.UTF_8)))
}
