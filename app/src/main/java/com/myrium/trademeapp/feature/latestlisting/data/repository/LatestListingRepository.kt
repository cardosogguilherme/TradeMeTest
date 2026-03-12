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
    val resolvedIsClassified = prices?.isClassified ?: isClassified ?: false
    val resolvedCurrentPrice = prices?.currentPrice ?: if (resolvedIsClassified) {
        rentPerWeek ?: startPrice ?: 0.0
    } else {
        startPrice ?: 0.0
    }
    val resolvedBuyNowPrice = prices?.buyNowPrice ?: buyNowPrice
    val resolvedImageUrl = imageUrl.orEmpty()
    val resolvedRegion = region.orEmpty()
    val resolvedTitle = title.orEmpty()

    return if (resolvedIsClassified) {
        ClassifiedListingEntity(
            imageUrl = resolvedImageUrl,
            region = resolvedRegion,
            title = resolvedTitle,
            askingPrice = resolvedCurrentPrice
        )
    } else {
        AuctionListingEntity(
            imageUrl = resolvedImageUrl,
            region = resolvedRegion,
            title = resolvedTitle,
            currentPrice = resolvedCurrentPrice,
            buyNowPrice = resolvedBuyNowPrice
        )
    }
}

private fun List<ListingResponse>.toEntity(): List<ListingEntity> {
    return map { it.toEntity() }
}
//endregion