package com.myrium.trademeapp.network

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import javax.inject.Inject

class LocalListingsInterceptor @Inject constructor(
    @ApplicationContext private val context: Context,
) : Interceptor {

    private companion object {
        //https://api.tmsandbox.co.nz/v1/listings/latest.json
        const val MOCK_HOST = "api.tmsandbox.co.nz/v1/"
        const val MOCK_PATH = "/listings/latest.json"
        const val MOCK_METHOD = "GET"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val isMockRequest = request.url.host == MOCK_HOST &&
            request.url.encodedPath == MOCK_PATH &&
            request.method.equals(MOCK_METHOD, ignoreCase = true)

        if (!isMockRequest) {
            return chain.proceed(request)
        }

        return try {
            val json = context.assets.open("realResponseListings.json")
                .bufferedReader()
                .use { it.readText() }

            Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .addHeader("content-type", "application/json")
                .body(json.toResponseBody("application/json".toMediaType()))
                .build()
        } catch (exception: IOException) {
            val fallback = "{\"error\":\"Unable to read realResponseListings.json\"}"
            Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(500)
                .message("Mock asset missing")
                .addHeader("content-type", "application/json")
                .body(fallback.toResponseBody("application/json".toMediaType()))
                .build()
        }
    }
}

