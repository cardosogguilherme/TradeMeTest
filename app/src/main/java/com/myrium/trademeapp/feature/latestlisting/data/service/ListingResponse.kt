package com.myrium.trademeapp.feature.latestlisting.data.service

import com.google.gson.annotations.SerializedName

data class ListingsResponse(
    @SerializedName("TotalCount")
    val totalCount: Int? = null,
    @SerializedName("Page")
    val page: Int? = null,
    @SerializedName("PageSize")
    val pageSize: Int? = null,
    @SerializedName(value = "listings", alternate = ["List"])
    val listings: List<ListingResponse>
)

data class ListingResponse(
    @SerializedName(value = "imageUrl", alternate = ["PictureHref"])
    val imageUrl: String? = null,
    @SerializedName(value = "region", alternate = ["Region"])
    val region: String? = null,
    @SerializedName(value = "title", alternate = ["Title"])
    val title: String? = null,
    @SerializedName("prices")
    val prices: PricesResponse? = null,
    @SerializedName("IsClassified")
    val isClassified: Boolean? = null,
    @SerializedName("StartPrice")
    val startPrice: Double? = null,
    @SerializedName("BuyNowPrice")
    val buyNowPrice: Double? = null,
    @SerializedName("RentPerWeek")
    val rentPerWeek: Double? = null,
)

data class PricesResponse(
    val isClassified: Boolean? = null,
    val currentPrice: Double? = null,
    val buyNowPrice: Double? = null
)