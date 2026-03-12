package com.myrium.trademeapp.network

import okhttp3.Interceptor
import okhttp3.Response
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.UUID

class OAuth1PlaintextInterceptor(
    private val consumerKey: String,
    private val consumerSecret: String,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (consumerKey.isBlank() || consumerSecret.isBlank()) {
            return chain.proceed(request)
        }

        if (request.header("Authorization") != null) {
            return chain.proceed(request)
        }

        val nonce = UUID.randomUUID().toString().replace("-", "")
        val timestamp = (System.currentTimeMillis() / 1000L).toString()
        val signature = "${percentEncode(consumerSecret)}&"

        val oauthParams = linkedMapOf(
            "oauth_consumer_key" to consumerKey,
            "oauth_nonce" to nonce,
            "oauth_signature" to signature,
            "oauth_signature_method" to "PLAINTEXT",
            "oauth_timestamp" to timestamp,
            "oauth_version" to "1.0",
        )

        val authorizationHeader = "OAuth " + oauthParams.entries.joinToString(", ") {
            "${it.key}=\"${percentEncode(it.value)}\""
        }

        val signedRequest = request.newBuilder()
            .header("Authorization", authorizationHeader)
            .build()

        return chain.proceed(signedRequest)
    }

    private fun percentEncode(value: String): String {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.name())
            .replace("+", "%20")
            .replace("*", "%2A")
            .replace("%7E", "~")
    }
}
