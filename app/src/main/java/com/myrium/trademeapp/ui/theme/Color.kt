package com.myrium.trademeapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)


// TradeMe colors from PDF
val Tasman500 = Color(0xFF148FE2)
val Feijoa500 = Color(0xFF29A754)
val BluffOyster800 = Color(0xFF393531)
val BluffOyster600 = Color(0xFF85807B)


class TradeMeColors(
    val tasman500: Color,
    val feijoa500: Color,
    val textDark: Color,
    val textLight: Color,
)

// Light theme colors
val LightTradeMeColors = TradeMeColors(
    tasman500 = Tasman500,
    feijoa500 = Feijoa500,
    textDark = BluffOyster800,
    textLight = BluffOyster600,
)

// Dark theme colors
val DarkTradeMeColors = TradeMeColors(
    tasman500 = Color(0xFF88c1e8),
    feijoa500 = Color(0xFF6dcd8d),
    textDark = Color(0xFF55504b),
    textLight = Color(0xFF6b6661),
)

// Select colors based on dark mode
@Composable
fun getTradeMeColors(): TradeMeColors {
    return if (isSystemInDarkTheme()) {
        DarkTradeMeColors
    } else {
        LightTradeMeColors
    }
}

