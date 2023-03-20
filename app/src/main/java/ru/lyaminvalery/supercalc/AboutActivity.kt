package ru.lyaminvalery.supercalc

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.lyaminvalery.supercalc.ui.AppScaffold

class AboutActivity : ComponentActivity() {

    private val currentThemeId = mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val currentId = remember {
                currentThemeId
            }
            AppScaffold(
                themeId = currentId.value,
                startAboutActivity = { },
                startSettingsActivity = { startActivity(Intent(context, SettingsActivity::class.java)) },
                captionText = stringResource(id = R.string.about)){
                    padding -> AboutScreen(padding)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        val preferences = getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE)
        currentThemeId.value = preferences.getInt(getString(R.string.theme_id_key), 0)
    }
}

@Composable
fun AboutScreen(padding: PaddingValues){
    Column(
        Modifier
            .padding(padding)
            .padding(8.dp)) {
        Text(stringResource(id = R.string.app_description_header),
            modifier = Modifier.padding(top=8.dp, bottom = 8.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp)
        Text(stringResource(id = R.string.about_text))
        Text(stringResource(id = R.string.dev_description_header),
            modifier = Modifier.padding(top=8.dp, bottom = 8.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp)
        Text(stringResource(id = R.string.dev_description))
        Image(painter = painterResource(id = R.drawable.logo_masu),
            contentDescription = stringResource(id = R.string.masu_logo_desc),
            modifier = Modifier.fillMaxWidth()
                .height(200.dp)
                .padding(top=16.dp, bottom = 8.dp),
            contentScale = ContentScale.FillHeight)
    }
}

