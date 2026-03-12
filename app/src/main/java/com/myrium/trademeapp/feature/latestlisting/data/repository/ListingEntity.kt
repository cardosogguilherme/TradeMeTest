package com.myrium.trademeapp.feature.latestlisting.data.repository

class ListingsEntity(val listings: List<ListingEntity>)

sealed class ListingEntity {
    abstract val imageUrl: String
    abstract val region: String
    abstract val title: String
}

data class AuctionListingEntity(
    override val imageUrl: String,
    override val region: String,
    override val title: String,
    val currentPrice: Double,
    val buyNowPrice: Double?,
) : ListingEntity()

data class ClassifiedListingEntity(
    override val imageUrl: String,
    override val region: String,
    override val title: String,
    val askingPrice: Double,
) : ListingEntity()

