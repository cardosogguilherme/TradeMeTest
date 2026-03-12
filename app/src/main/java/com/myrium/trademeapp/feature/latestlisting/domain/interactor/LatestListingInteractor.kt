package com.myrium.trademeapp.feature.latestlisting.domain.interactor

import com.myrium.trademeapp.feature.latestlisting.data.repository.AuctionListingEntity
import com.myrium.trademeapp.feature.latestlisting.data.repository.ClassifiedListingEntity
import com.myrium.trademeapp.feature.latestlisting.data.repository.LatestListingRepository
import com.myrium.trademeapp.feature.latestlisting.data.repository.ListingEntity
import com.myrium.trademeapp.feature.latestlisting.data.repository.ListingsEntity
import com.myrium.trademeapp.feature.latestlisting.presentation.viewmodel.ListingsState
import javax.inject.Inject

class LatestListingInteractor @Inject constructor(
    private val latestListingRepository: LatestListingRepository,
) {
    suspend fun fetchLatestListings(): ListingsViewItem {
        return latestListingRepository.getLatestListings().toViewItem()
    }
}


// region data
data class ListingsViewItem(
    val listings: List<ListingViewItem>
)

data class ListingViewItem(
    val imageUrl: String,
    val region: String,
    val title: String,
    val prices: PricesViewItem
)

data class PricesViewItem(
    val isClassified: Boolean,
    val currentPrice: Double,
    val buyNowPrice: Double? = null
)
// endregion


// region Mapper

private fun ListingsEntity.toViewItem(): ListingsViewItem {
    return ListingsViewItem(listings = listings.map { it.toViewItem() })
}

private fun ListingEntity.toViewItem(): ListingViewItem {
    return ListingViewItem(
        imageUrl = imageUrl,
        region = region,
        title = title,
        prices = PricesViewItem(
            isClassified = this is ClassifiedListingEntity,
            currentPrice = when (this) {
                is ClassifiedListingEntity -> askingPrice
                is AuctionListingEntity -> currentPrice
            },
            buyNowPrice = if (this is AuctionListingEntity) buyNowPrice else null
        )
    )
}
// endregion
