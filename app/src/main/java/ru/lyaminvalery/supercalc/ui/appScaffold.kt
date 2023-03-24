package ru.lyaminvalery.supercalc.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import ru.lyaminvalery.supercalc.R
import ru.lyaminvalery.supercalc.ui.theme.SuperCalcTheme

@Composable
fun AppScaffold(themeId: Int = 0,
                darkTheme: Boolean = isSystemInDarkTheme(),
                startAboutActivity: () -> Unit,
                startSettingsActivity: () -> Unit,
                captionText: String,
                content: @Composable (PaddingValues) -> Unit){

    SuperCalcTheme(themeId = themeId, darkTheme = darkTheme) {
        Scaffold(

            topBar = {
                TopAppBar {

                    val menuExpanded = remember {
                        mutableStateOf(false)
                    }

                    IconButton(onClick = { menuExpanded.value = true}) {
                        Icon(Icons.Filled.Menu, contentDescription = stringResource(R.string.menu))
                    }
                    Text(captionText, fontSize = 22.sp)

                    DropdownMenu(
                        expanded = menuExpanded.value,
                        onDismissRequest = { menuExpanded.value = false },
                    ) {
                        // 6
                        DropdownMenuItem(
                            onClick = {
                                menuExpanded.value = false
                                startAboutActivity() },
                        ){
                            Text(stringResource(R.string.about))
                        }
                    }

                    Spacer(Modifier.weight(1f, true))

                    IconButton(onClick = startSettingsActivity) {

                        Icon(
                            Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.settings_btn_text) )
                    }

                }


            }
        ) {
                padding -> content(padding)
        }
    }
}