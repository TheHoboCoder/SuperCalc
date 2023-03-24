package ru.lyaminvalery.supercalc

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.lyaminvalery.supercalc.ui.*
import ru.lyaminvalery.supercalc.viewmodel.CalcViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CalcViewModel by viewModels()
    // TODO(create shared preferences wrapper)
    private val currentThemeId = mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val themeId = remember {
                currentThemeId
            }
            AppScaffold(
                themeId = themeId.value,
                startAboutActivity = { startActivity(Intent(context, AboutActivity::class.java)) },
                startSettingsActivity = { startActivity(Intent(context, SettingsActivity::class.java)) },
                captionText = stringResource(id = R.string.app_name)) {
                    padding -> MainStack(padding)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val preferences = getSharedPreferences(getString(R.string.shared_preferences), MODE_PRIVATE)
        currentThemeId.value = preferences.getInt(getString(R.string.theme_id_key), 0)
    }

    @Composable
    fun ModeSwitcher(
        modeIds: List<Int>,
        changeMode: (Int) -> Unit){
        val tabIndex = remember { mutableStateOf(0) }

        TabRow(selectedTabIndex = tabIndex.value){
            modeIds.forEachIndexed {
                index, value ->
                Tab(modifier = Modifier.padding(8.dp),
                    selected = tabIndex.value == index,
                    onClick = {
                        tabIndex.value = index
                        changeMode(index)
                    }
                ){
                    Text(getString(value))
                }
            }
        }

    }


    @Composable
    fun MainStack(padding: PaddingValues){

        Column(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {

            val inputVal = remember { viewModel.inputText }
            val outputVal = remember { viewModel.outputText }
            val inputPosition = remember { viewModel.currentPos }
            val isFailed = remember { viewModel.isFailed }
            val numpadButtons = remember { viewModel.numpadButtons }
            val allowedTokens = remember { viewModel.allowedTokens }

            val primaryColumn = remember { viewModel.primaryColumn }
            val secondaryColumn = remember { viewModel.secondaryColumn }
            val mainRow = remember { viewModel.primaryRow }
            val additionalRows = remember { viewModel.additionalRows }
            val isRadixValid = remember {
                viewModel.isRadixValid
            }


            ModeSwitcher(
                modeIds = listOf(R.string.mode_plain, R.string.mode_scientific, R.string.mode_programmer),
                changeMode = { viewModel.changeMode(CalcViewModel.Mode.values()[it])})


            val topPaneWeight = if(viewModel.currentMode.value != CalcViewModel.Mode.PLAIN) 3f else 2f
            TopPane(modifier = Modifier
                .fillMaxHeight()
                .weight(topPaneWeight),
                    inputText = inputVal.value,
                    outputText = outputVal.value,
                    hasError = isFailed.value,
                    textPosition = inputPosition.value,
                    setTextPosition = viewModel::setInputPosition,
                    includeRadix = viewModel.currentMode.value == CalcViewModel.Mode.PROGRAMMER,
                    radix = viewModel.radix.value,
                    setRadix = viewModel::setRadix,
                    isRadixValid = isRadixValid.value)

            if(additionalRows.value.isNotEmpty()){
                for(row in additionalRows.value){
                    ButtonRow(modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .weight(1f),
                        states = row, allowedTokens = allowedTokens.value)
                }
            }

            ButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .weight(1f),
                states = mainRow.value,
                allowedTokens = allowedTokens.value
            )

            Row(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .weight(4f)){

                val modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()

                if(secondaryColumn.value.isNotEmpty()){
                    ButtonColumn(modifier = Modifier
                        .then(modifier)
                        .weight(1f),
                        states = secondaryColumn.value,
                        allowedTokens = allowedTokens.value)
                }


                Numpad(modifier = Modifier
                    .then(modifier)
                    .weight(3f),
                    buttons = numpadButtons.value,
                    allowedTokens = allowedTokens.value,
                    setText = viewModel::setText)

                ButtonColumn(modifier = Modifier
                    .then(modifier)
                    .weight(1f),
                    states = primaryColumn.value,
                    allowedTokens = allowedTokens.value)
            }


        }
    }
}










