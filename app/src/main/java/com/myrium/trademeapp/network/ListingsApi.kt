package com.myrium.trademeapp.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface ListingsApi {
    @GET("listings/latest.json")
    suspend fun getListings(): Response<ResponseBody>
}

