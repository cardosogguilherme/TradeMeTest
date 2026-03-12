package com.myrium.trademeapp.feature.latestlisting.data.service

import com.google.gson.Gson
import com.myrium.trademeapp.network.ListingsApi
import javax.inject.Inject

class LatestListingService @Inject constructor(
    private val listingsApi: ListingsApi,
    private val gson: Gson,
) {
    suspend fun getLatestListings(): ListingsResponse {
        val response = listingsApi.getListings()
        return if (response.isSuccessful) {
            val body = response.body()?.string() ?: throw Exception("Empty response body")
            gson.fromJson(body, ListingsResponse::class.java)
        } else {
            throw Exception("API Error: ${response.code()} - ${response.message()}")
        }
    }
}

