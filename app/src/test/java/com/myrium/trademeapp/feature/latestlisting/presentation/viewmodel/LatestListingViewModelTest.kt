package com.myrium.trademeapp.feature.latestlisting.presentation.viewmodel

import com.google.gson.Gson
import com.myrium.trademeapp.feature.latestlisting.data.repository.LatestListingRepository
import com.myrium.trademeapp.feature.latestlisting.data.service.LatestListingService
import com.myrium.trademeapp.feature.latestlisting.domain.interactor.LatestListingInteractor
import com.myrium.trademeapp.network.ListingsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class LatestListingViewModelTest {

    @get:Rule
    val mainDispatcherRule: TestWatcher = MainDispatcherRule()

    @Test
    fun `loadLatestListings emits Success when api returns valid payload`() = runTest {
        val jsonPayload =
            """
            {
              "TotalCount": 2,
              "Page": 1,
              "PageSize": 50,
              "List": [
                {
                  "ListingId": 1,
                  "Title": "Auction listing",
                  "PictureHref": "https://example.com/auction.jpg",
                  "Region": "Auckland",
                  "StartPrice": 50.0,
                  "BuyNowPrice": 80.0,
                  "HasBuyNow": true,
                  "PriceDisplay": "${'$'}80.00"
                },
                {
                  "ListingId": 2,
                  "Title": "Classified listing",
                  "PictureHref": "https://example.com/classified.jpg",
                  "Region": "Wellington",
                  "StartPrice": 120.0,
                  "IsClassified": true,
                  "PriceDisplay": "${'$'}120.00"
                }
              ]
            }
            """.trimIndent()

        val viewModel = createViewModel(
            api = FakeListingsApi.JsonSuccess(jsonPayload)
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is LatestListingState.Success)

        val successState = state as LatestListingState.Success
        assertEquals(2, successState.listings.listings.size)

        val auction = successState.listings.listings[0]
        assertFalse(auction.prices.isClassified)
        assertEquals(50.0, auction.prices.currentPrice, 0.0)
        assertTrue(auction.prices.buyNowPrice != null)
        assertEquals(80.0, auction.prices.buyNowPrice ?: -1.0, 0.0)

        val classified = successState.listings.listings[1]
        assertTrue(classified.prices.isClassified)
        assertEquals(120.0, classified.prices.currentPrice, 0.0)
        assertNull(classified.prices.buyNowPrice)
    }

    @Test
    fun `loadLatestListings emits Error when api returns failure response`() = runTest {
        val viewModel = createViewModel(
            api = FakeListingsApi.Success(
                response = Response.error(
                    500,
                    "internal error".toResponseBody("text/plain".toMediaType())
                )
            )
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is LatestListingState.Error)
        assertTrue((state as LatestListingState.Error).message.contains("API Error: 500"))
    }

    @Test
    fun `loadLatestListings emits Error when api throws exception`() = runTest {
        val viewModel = createViewModel(
            api = FakeListingsApi.Failure(IOException("network down"))
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is LatestListingState.Error)
        assertEquals("network down", (state as LatestListingState.Error).message)
    }

    @Test
    fun `initial success is capped to page size of 20`() = runTest {
        val payload = buildListingsPayload(25)
        val viewModel = createViewModel(
            api = FakeListingsApi.JsonSuccess(payload)
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is LatestListingState.Success)
        val listings = (state as LatestListingState.Success).listings.listings
        assertEquals(20, listings.size)
        assertEquals("Listing 0", listings.first().title)
        assertEquals("Listing 19", listings.last().title)
    }

    @Test
    fun `loadNextPage appends remaining items`() = runTest {
        val payload = buildListingsPayload(25)
        val viewModel = createViewModel(
            api = FakeListingsApi.JsonSuccess(payload)
        )

        advanceUntilIdle()
        viewModel.loadNextPage()

        val state = viewModel.uiState.value
        assertTrue(state is LatestListingState.Success)
        val listings = (state as LatestListingState.Success).listings.listings
        assertEquals(25, listings.size)
        assertEquals("Listing 24", listings.last().title)
    }

    @Test
    fun `reload resets to first page after loading next page`() = runTest {
        val api = FakeListingsApi.JsonSuccess(buildListingsPayload(30))
        val viewModel = createViewModel(api = api)

        advanceUntilIdle()
        viewModel.loadNextPage()
        assertEquals(30, (viewModel.uiState.value as LatestListingState.Success).listings.listings.size)

        viewModel.reload()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is LatestListingState.Success)
        val listings = (state as LatestListingState.Success).listings.listings
        assertEquals(20, listings.size)
        assertEquals("Listing 19", listings.last().title)
    }

    private fun buildListingsPayload(count: Int): String {
        val listingsJson = (0 until count).joinToString(separator = ",") { index ->
            val isClassified = index % 3 == 0
            val buyNowPriceField = if (isClassified) {
                ""
            } else {
                "\n              \"BuyNowPrice\": ${200 + index}.0,\n              \"HasBuyNow\": true,"
            }
            val isClassifiedField = if (isClassified) {
                "\n              \"IsClassified\": true,"
            } else {
                ""
            }
            """
            {
              "ListingId": ${1000 + index},
              "Title": "Listing $index",
              "PictureHref": "https://example.com/$index.jpg",
              "Region": "Region $index",
              "StartPrice": ${100 + index}.0,$buyNowPriceField$isClassifiedField
              "PriceDisplay": "${'$'}${100 + index}.00"
            }
            """.trimIndent()
        }

        return """
            {
              "TotalCount": $count,
              "Page": 1,
              "PageSize": 50,
              "List": [
                $listingsJson
              ]
            }
        """.trimIndent()
    }

    private fun createViewModel(api: ListingsApi): LatestListingViewModel {
        val service = LatestListingService(listingsApi = api, gson = Gson())
        val repository = LatestListingRepository(latestListingService = service)
        val interactor = LatestListingInteractor(latestListingRepository = repository)
        return LatestListingViewModel(latestListingInteractor = interactor)
    }
}

private sealed class FakeListingsApi : ListingsApi {
    data class Success(private val response: Response<ResponseBody>) : FakeListingsApi() {
        override suspend fun getListings(): Response<ResponseBody> = response
    }

    data class JsonSuccess(private val payload: String) : FakeListingsApi() {
        override suspend fun getListings(): Response<ResponseBody> {
            return Response.success(payload.toResponseBody("application/json".toMediaType()))
        }
    }

    data class Failure(private val throwable: Throwable) : FakeListingsApi() {
        override suspend fun getListings(): Response<ResponseBody> {
            throw throwable
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private class MainDispatcherRule(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}


