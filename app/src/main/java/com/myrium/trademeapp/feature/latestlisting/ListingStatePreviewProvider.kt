package com.myrium.trademeapp.feature.latestlisting

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.myrium.trademeapp.feature.latestlisting.presentation.viewmodel.ListingState
import com.myrium.trademeapp.feature.latestlisting.presentation.viewmodel.PricesState

class ListingStatePreviewProvider : PreviewParameterProvider<ListingState> {
    override val values: Sequence<ListingState> = sequenceOf(
        ListingState(
            imageUrl = "https://example.com/listing-auction.jpg",
            region = "Auckland",
            title = "Vintage road bike in great condition",
            prices = PricesState(
                isClassified = false,
                currentPrice = 420.0,
                buyNowPrice = 560.0,
            ),
        ),
        ListingState(
            imageUrl = "https://example.com/listing-classified.jpg",
            region = "Wellington",
            title = "Sunny two-bedroom apartment for rent",
            prices = PricesState(
                isClassified = true,
                currentPrice = 695.0,
                buyNowPrice = null,
            ),
        ),
    )
}