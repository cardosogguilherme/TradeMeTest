package com.myrium.trademeapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.myrium.trademeapp.feature.latestlisting.presentation.viewmodel.LatestListingScreen
import com.myrium.trademeapp.feature.latestlisting.presentation.viewmodel.LatestListingState
import com.myrium.trademeapp.feature.latestlisting.presentation.viewmodel.LatestListingViewModel
import com.myrium.trademeapp.feature.latestlisting.presentation.viewmodel.ListingState
import com.myrium.trademeapp.feature.latestlisting.presentation.viewmodel.ListingsState
import com.myrium.trademeapp.feature.latestlisting.presentation.viewmodel.PricesState
import com.myrium.trademeapp.feature.profile.MyTradeScreen
import com.myrium.trademeapp.feature.watchlist.WatchlistScreen
import com.myrium.trademeapp.ui.theme.TradeMeAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: LatestListingViewModel by viewModels<LatestListingViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TradeMeAppTheme {
                viewModel.uiState.collectAsStateWithLifecycle().value.let { state ->
                    TradeMeScreen(
                        state = state,
                        onRefresh = viewModel::reload,
                        onLoadNextPage = viewModel::loadNextPage,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeMeScreen(
    state: LatestListingState,
    onRefresh: () -> Unit = {},
    onLoadNextPage: () -> Unit = {},
) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.LATEST_LISTINGS) }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            painterResource(it.icon),
                            contentDescription = it.label
                        )
                    },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        if (currentDestination == AppDestinations.LATEST_LISTINGS && isSearchActive) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 8.dp),
                                placeholder = { Text("Search listings...") },
                                singleLine = true
                            )
                        } else {
                            Text(currentDestination.label)
                        }
                    },
                    actions = {
                        if (currentDestination == AppDestinations.LATEST_LISTINGS) {
                            IconButton(
                                onClick = { isSearchActive = !isSearchActive }
                            ) {
                                Icon(painter = painterResource(R.drawable.search), contentDescription = "Search")
                            }
                            IconButton(
                                onClick = {
                                    Toast.makeText(context, "Cart clicked!", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(painter = painterResource(R.drawable.cart), contentDescription = "Cart")
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            when (currentDestination) {
                AppDestinations.LATEST_LISTINGS -> {
                    LatestListingScreen(
                        state = state,
                        onRefresh = onRefresh,
                        onLoadNextPage = onLoadNextPage,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                AppDestinations.WATCHLIST -> {
                    WatchlistScreen(modifier = Modifier.padding(innerPadding))
                }
                AppDestinations.MY_TRADE_ME -> {
                    MyTradeScreen(modifier = Modifier.padding(innerPadding))
                }
            }

        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: Int,
) {
    LATEST_LISTINGS("Latest Listings", R.drawable.cart),
    WATCHLIST("Watchlist", R.drawable.ic_binoculars),
    MY_TRADE_ME("My Trade Me", R.drawable.profile),
}

// region Previews

// Sample data for previews
private val sampleTitles = listOf(
    "Vintage road bike in great condition",
    "Sunny two-bedroom apartment for rent",
    "iPhone 14 Pro Max - Like new",
    "Leather sofa in excellent condition",
    "Mountain bike with accessories",
    "Gaming laptop - High performance",
    "Coffee table - Modern design",
    "Vintage vinyl record collection",
    "Electric scooter - Barely used",
    "Designer handbag - Authentic",
    "Office chair - Ergonomic",
    "Drone with 4K camera",
    "Bookshelf - Solid wood",
    "Smart TV 55 inch",
    "Bicycle helmet - Safety certified"
)

private val newZealandRegions = listOf(
    "Auckland",
    "Wellington",
    "Christchurch",
    "Hamilton",
    "Tauranga",
    "Dunedin",
    "Palmerston North",
    "Rotorua",
    "Napier",
    "Lower Hutt"
)

fun generateRandomListings(count: Int = 10): List<ListingState> {
    val random = Random(System.currentTimeMillis())
    return (1..count).map {
        val isClassified = random.nextBoolean()
        val currentPrice = random.nextDouble(50.0, 2000.0)

        ListingState(
            imageUrl = "https://example.com/listing-$it.jpg",
            region = newZealandRegions.random(random),
            title = sampleTitles.random(random),
            prices = PricesState(
                isClassified = isClassified,
                currentPrice = currentPrice,
                buyNowPrice = if (isClassified) null else random.nextDouble(currentPrice + 50, currentPrice + 500)
            )
        )
    }
}

@Preview(name = "TradeMe", showBackground = true)
@Composable
fun TradeMeScreenPreview() {
    TradeMeAppTheme {
        TradeMeScreen(LatestListingState.Success(
            ListingsState(
                listings = generateRandomListings(10)
            )
        ))
    }
}

// endregion