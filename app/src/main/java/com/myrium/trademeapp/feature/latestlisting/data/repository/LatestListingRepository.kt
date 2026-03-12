package com.myrium.trademeapp.feature.latestlisting.data.repository

import com.myrium.trademeapp.feature.latestlisting.data.service.ListingResponse
import com.myrium.trademeapp.feature.latestlisting.data.service.LatestListingService
import com.myrium.trademeapp.feature.latestlisting.data.service.ListingsResponse
import javax.inject.Inject

class LatestListingRepository @Inject constructor(
    private val latestListingService: LatestListingService,
) {
    suspend fun getLatestListings(): ListingsEntity {
        val responseDto = latestListingService.getLatestListings()
        return responseDto.toEntity()
    }
}

// region Mapper
private fun ListingsResponse.toEntity(): ListingsEntity {
    return ListingsEntity(listings = listings.toEntity())
}

private fun ListingResponse.toEntity(): ListingEntity {
    return if (prices.isClassified) {
        ClassifiedListingEntity(
            imageUrl = imageUrl,
            region = region,
            title = title,
            askingPrice = prices.currentPrice
        )
    } else {
        AuctionListingEntity(
            imageUrl = imageUrl,
            region = region,
            title = title,
            currentPrice = prices.currentPrice,
            buyNowPrice = prices.buyNowPrice
        )
    }
}

private fun List<ListingResponse>.toEntity(): List<ListingEntity> {
    return map { it.toEntity() }
}
//endregion