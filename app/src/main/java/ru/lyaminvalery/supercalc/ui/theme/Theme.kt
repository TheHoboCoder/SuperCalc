package ru.lyaminvalery.supercalc.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import ru.lyaminvalery.supercalc.R


private val WarmDarkColorPalette = darkColors(
    primary = colorYellow,
    primaryVariant = colorOrange,
    secondary = colorLighterGrey,
    onPrimary = colorWhite,
    onError = colorRed,
    onSurface = colorWhite
)

private val WarmLightColorPalette = lightColors(
    primary = colorYellow,
    primaryVariant = colorOrange,
    secondary = colorDarkGrey,
    onPrimary = colorDarkBrown,
    onError = colorRed,
    onSurface = colorDarkBrown
)

private val ColdLightColorPalette = lightColors(
    primary = colorDarkBlue,
    primaryVariant = colorT,
    secondary = colorLightBlue,
    onPrimary = colorBlueWhite,
    onError = colorRed,
    onSurface = colorDarkBrown
)

private val ColdDarkColorPalette = darkColors(
    primary = colorDarkBlue,
    primaryVariant = colorT,
    secondary = colorLightBlue,
    onPrimary = colorWhite,
    onError = colorRed,
    onSurface = colorWhite
)

class ThemeHolder(val id: Int,
                  val resId: Int,
                  val lightColors: Colors,
                  val darkColors: Colors){

    fun getColors(darkTheme: Boolean) =
        if(darkTheme) darkColors else lightColors

    @Composable
    fun Theme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit){
        MaterialTheme(
            colors = getColors(darkTheme),
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}

val THEMES = arrayOf<ThemeHolder>(
    ThemeHolder(0, R.string.theme_warm, WarmLightColorPalette, WarmDarkColorPalette),
    ThemeHolder(1, R.string.theme_cold, ColdLightColorPalette, ColdDarkColorPalette)
).associateBy { it.id }

@Composable
fun SuperCalcTheme(themeId: Int = 0, darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    THEMES[themeId]!!.Theme(darkTheme = darkTheme, content = content)
}