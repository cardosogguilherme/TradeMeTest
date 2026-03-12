package com.myrium.trademeapp.feature.latestlisting.presentation.viewmodel

import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myrium.trademeapp.R
import com.myrium.trademeapp.feature.latestlisting.ListingStatePreviewProvider
import com.myrium.trademeapp.ui.theme.TradeMeAppTheme
import com.myrium.trademeapp.ui.theme.getTradeMeColors
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.random.Random.Default.nextBoolean

@Composable
fun LatestListingScreen(
    state: LatestListingState,
    onRefresh: () -> Unit,
    onLoadNextPage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    var lastRequestedCount by remember { mutableIntStateOf(0) }

    val loadedCount = (state as? LatestListingState.Success)?.listings?.listings?.size ?: 0

    LaunchedEffect(loadedCount) {
        if (loadedCount < lastRequestedCount) {
            lastRequestedCount = loadedCount
        }
    }

    LaunchedEffect(state, listState) {
        if (state !is LatestListingState.Success) return@LaunchedEffect
        snapshotFlow {
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val totalItemsCount = state.listings.listings.size
            lastVisibleItemIndex to totalItemsCount
        }
            .distinctUntilChanged()
            .collect { (lastVisibleItemIndex, totalItemsCount) ->
                if (totalItemsCount == 0) return@collect
                val shouldLoadMore = lastVisibleItemIndex >= totalItemsCount - 5
                if (shouldLoadMore && totalItemsCount > lastRequestedCount) {
                    lastRequestedCount = totalItemsCount
                    onLoadNextPage()
                }
            }
    }

    PullToRefreshBox(
        isRefreshing = state is LatestListingState.Loading,
        onRefresh = onRefresh,
        modifier = modifier,
    ) {
        if (state is LatestListingState.Success) {
            LazyColumn(state = listState) {

                items(state.listings.listings.size) { index ->
                    val activity = LocalActivity.current
                    LatestListingItem(item = state.listings.listings[index]) {
                        Toast
                            .makeText(activity, "Clicked on ${state.listings.listings[index].title}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }
}

@Composable
fun LatestListingItem(item: ListingState, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = "Listing Image",
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(getTradeMeColors().feijoa500)
                .size(80.dp)
        )
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .requiredHeightIn(min = 80.dp)
                .weight(1f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = item.region,
                    color = getTradeMeColors().textLight,
                    fontSize = 10.sp,
                    lineHeight = 10.sp
                )
                Text(
                    text = item.title,
                    color = getTradeMeColors().textDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 16.sp
                )
            }

            ListingPrice(
                pricesState = item.prices
            )
        }
    }
}

@Composable
fun ListingPrice(pricesState: PricesState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        // The if statement is inside the column given the designs show they take space even
        // if not an auction
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Start
        ) {
            if (!pricesState.isClassified) {
                Text(
                    text = stringResource(R.string.price_format, pricesState.currentPrice.formatPrice()),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                // Given there was no rule about reserve, I'll add here randomly
                Text(
                    text = if (nextBoolean()) stringResource(R.string.reserve_not_met)
                    else stringResource(R.string.reserve_met),
                    color = getTradeMeColors().textLight,
                    fontSize = 10.sp,
                    lineHeight = 10.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom,
        ) {
            if (!pricesState.isClassified && pricesState.buyNowPrice == null) return@Column

            val price = if (pricesState.isClassified) pricesState.currentPrice else pricesState.buyNowPrice!!
            Text(
                text = stringResource(R.string.price_format, price.formatPrice()),
                color = getTradeMeColors().textDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.buy_now),
                color = getTradeMeColors().textLight,
                fontSize = 10.sp,
                lineHeight = 10.sp
            )
        }
    }
}

private fun Double.formatPrice(): String = "%.2f".format(this)

@Preview
@Composable
fun LatestListingItemPreview(
    @PreviewParameter(ListingStatePreviewProvider::class) item: ListingState,
) {
    TradeMeAppTheme {
        LatestListingItem(item = item) {}
    }
}