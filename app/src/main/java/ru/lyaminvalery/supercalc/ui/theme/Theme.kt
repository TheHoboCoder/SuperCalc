package ru.lyaminvalery.supercalc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = primaryDark,
    primaryVariant = primaryVariantDark,
    secondary = secondaryDark,
    onPrimary = onPrimaryDark,
    onError = errorColor
)

private val LightColorPalette = lightColors(
    primary = primaryLight,
    primaryVariant = primaryVariantDark,
    secondary = secondaryLight,
    onPrimary = onPrimaryLight,
    onError = errorColor
)

@Composable
fun SuperCalcTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}