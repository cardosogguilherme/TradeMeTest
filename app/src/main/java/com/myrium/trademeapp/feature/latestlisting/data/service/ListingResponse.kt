package com.myrium.trademeapp.feature.latestlisting.data.service

data class ListingsResponse(
    val listings: List<ListingResponse>
)

data class ListingResponse(
    val imageUrl: String,
    val region: String,
    val title: String,
    val prices: PricesResponse
)

data class PricesResponse(
    val isClassified: Boolean,
    val currentPrice: Double,
    val buyNowPrice: Double? = null
)