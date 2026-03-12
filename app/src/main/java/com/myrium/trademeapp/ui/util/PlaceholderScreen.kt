package com.myrium.trademeapp.ui.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.myrium.trademeapp.ui.theme.TradeMeAppTheme
import com.myrium.trademeapp.ui.theme.getTradeMeColors

@Composable
fun PlaceholderScreen(modifier: Modifier = Modifier, text: String) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(getTradeMeColors().feijoa500)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(com.myrium.trademeapp.R.drawable.ic_launcher_foreground),
            contentDescription = "Placeholder Image",
            modifier = Modifier
                .size(300.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "You navigated to $text",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Preview
@Composable
fun Preview_PlaceholderScreen() {
    TradeMeAppTheme {
        PlaceholderScreen(text = "Placeholder Screen")
    }
}