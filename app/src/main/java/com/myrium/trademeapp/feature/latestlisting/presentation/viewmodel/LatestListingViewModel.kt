package com.myrium.trademeapp.feature.latestlisting.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myrium.trademeapp.feature.latestlisting.data.repository.ListingEntity
import com.myrium.trademeapp.feature.latestlisting.domain.interactor.LatestListingInteractor
import com.myrium.trademeapp.feature.latestlisting.domain.interactor.ListingViewItem
import com.myrium.trademeapp.feature.latestlisting.domain.interactor.ListingsViewItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


// region data
sealed class LatestListingState {
    data object Loading : LatestListingState()
    data class Success(val listings: ListingsState) : LatestListingState()
    data class Error(val message: String) : LatestListingState()
}

data class ListingsState(
    val listings: List<ListingState>
)

data class ListingState(
    val imageUrl: String,
    val region: String,
    val title: String,
    val prices: PricesState
)

data class PricesState(
    val isClassified: Boolean,
    val currentPrice: Double,
    val buyNowPrice: Double? = null
)
// endregion


@HiltViewModel
class LatestListingViewModel @Inject constructor(
    private val latestListingInteractor: LatestListingInteractor,
) : ViewModel() {

    private companion object {
        const val PAGE_SIZE = 20
    }

    private val _uiState = MutableStateFlow<LatestListingState>(LatestListingState.Loading)
    val uiState: StateFlow<LatestListingState> = _uiState.asStateFlow()

    private var allListings: List<ListingViewItem> = emptyList()
    private var currentPage: Int = 1

    init {
        loadLatestListings()
    }

    private fun loadLatestListings() {
        viewModelScope.launch {
            try {
                _uiState.value = LatestListingState.Loading
                val listings = latestListingInteractor.fetchLatestListings()
                allListings = listings.listings
                currentPage = 1
                emitPagedSuccess()
            } catch (exception: Exception) {
                _uiState.value = LatestListingState.Error(
                    exception.message ?: "Unknown error occurred"
                )
            }
        }
    }


    fun loadNextPage() {
        if (allListings.isEmpty()) return
        val nextPage = currentPage + 1
        val nextLimit = nextPage * PAGE_SIZE
        if (nextLimit - PAGE_SIZE >= allListings.size) return
        currentPage = nextPage
        emitPagedSuccess()
    }


    fun reload() {
        loadLatestListings()
    }

    private fun emitPagedSuccess() {
        val pagedListings = allListings.take(currentPage * PAGE_SIZE)
        _uiState.value = LatestListingState.Success(ListingsState(listings = pagedListings.map { it.toState() }))
    }


    private fun ListingViewItem.toState(): ListingState =
        ListingState(
            imageUrl = imageUrl,
            region = region,
            title = title,
            prices = PricesState(
                isClassified = prices.isClassified,
                currentPrice = prices.currentPrice,
                buyNowPrice = prices.buyNowPrice
            )
        )
}

