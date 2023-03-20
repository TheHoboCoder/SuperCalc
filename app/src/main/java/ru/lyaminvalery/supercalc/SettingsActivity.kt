package ru.lyaminvalery.supercalc

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.lyaminvalery.supercalc.ui.AppScaffold
import ru.lyaminvalery.supercalc.ui.theme.SuperCalcTheme
import ru.lyaminvalery.supercalc.ui.theme.THEMES
import ru.lyaminvalery.supercalc.ui.theme.ThemeHolder

class SettingsActivity : ComponentActivity() {

    private val currentThemeId = mutableStateOf(0)
    private lateinit var preferences: SharedPreferences

    private fun setCurrentTheme(id: Int){
        val editor = preferences.edit()
        editor.putInt(getString(R.string.theme_id_key), id)
        currentThemeId.value = id
        editor.apply()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE)
        currentThemeId.value = preferences.getInt(getString(R.string.theme_id_key), 0)
        setContent {
            val context = LocalContext.current
            val currentId = remember {
                currentThemeId
            }

            AppScaffold(themeId = currentId.value,
                startAboutActivity = { startActivity(Intent(context, AboutActivity::class.java)) },
                startSettingsActivity = {  },
                captionText = stringResource(id = R.string.settings_text)
            ) {
                padding -> SettingsContent(paddingValues = padding,
                        themes = THEMES,
                        themeId = currentId.value,
                        setTheme = this::setCurrentTheme)
            }
        }
    }


}

@Composable
fun SettingsContent(paddingValues: PaddingValues,
                    themes: Map<Int, ThemeHolder>,
                    themeId: Int,
                    setTheme: (Int) -> Unit){
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)){
        Text(stringResource(id = R.string.theme_select),
            modifier = Modifier.padding(all=8.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp)
        themes.forEach { (i, themeHolder) ->
            Row(verticalAlignment = Alignment.CenterVertically){
                RadioButton(selected = themeId == i, onClick = { setTheme(i) })
                Text(stringResource(id = themeHolder.resId))
            }

            val colors = themeHolder.getColors(isSystemInDarkTheme())

            Card(modifier = Modifier
                .height(100.dp)
                .padding(start = 16.dp, end = 16.dp), elevation = 8.dp) {
                Row(Modifier.fillMaxWidth()) {
                    val boxModifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .weight(1f)
                    Box(
                        Modifier
                            .then(boxModifier)
                            .background(color = colors.primary))
                    Box(
                        Modifier
                            .then(boxModifier)
                            .background(color = colors.primaryVariant))
                    Box(
                        Modifier
                            .then(boxModifier)
                            .background(color = colors.secondary))
                    Box(
                        Modifier
                            .then(boxModifier)
                            .background(color = colors.onPrimary))
                }


            }
           
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SuperCalcTheme {
        Greeting("Android")
    }
}