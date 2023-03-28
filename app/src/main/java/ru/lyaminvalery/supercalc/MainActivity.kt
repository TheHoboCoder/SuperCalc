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
import androidx.compose.ui.text.AnnotatedString
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
    fun ControlPanel(modifier: Modifier,
                     setText: (String) -> Unit,
                     doBackspace: () -> Unit,
                     doClear: () -> Unit,
                     isPlain: Boolean = true){
        Row(modifier){
            if(isPlain){

                CalcButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    text = AnnotatedString("%"),
                    onClick = { setText("%") }
                )

            }
            else{

                CalcButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    text = AnnotatedString("("),
                    onClick = { setText("(") }
                )

                CalcButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    text = AnnotatedString(")"),
                    onClick = { setText(")") }
                )
            }

            CalcButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                text = AnnotatedString("C"),
                onClick = doClear,
                isControl = true
            )

            CalcButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                text = AnnotatedString("⌫"),
                onClick = doBackspace,
                isControl = true
            )

            CalcButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                text = AnnotatedString("/"),
                onClick = { setText("/") },
            )

        }
    }

    @Composable
    fun MainOperatorsPanel(modifier: Modifier,
                           setText: (String) -> Unit,
                           compute: () -> Unit){

        Column(modifier = modifier){

            for(operator in listOf("*", "+", "-")){
                CalcButton(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    text = AnnotatedString(operator),
                    onClick = { setText(operator) })
            }

            CalcButton(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                text = AnnotatedString("="),
                onClick = compute,
                isControl = true
            )

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

            val isRadixValid = remember {
                viewModel.isRadixValid
            }


            ModeSwitcher(
                modeIds = listOf(R.string.mode_plain, R.string.mode_scientific, R.string.mode_programmer),
                changeMode = { viewModel.changeMode(CalcViewModel.Mode.values()[it])})


            val topPaneWeight = if(viewModel.currentMode.value != CalcViewModel.Mode.PLAIN) 3f else 2f
            TopPane(
                modifier = Modifier
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
                isRadixValid = isRadixValid.value
            )

            if(viewModel.currentMode.value != CalcViewModel.Mode.PLAIN){
                if(viewModel.currentMode.value == CalcViewModel.Mode.SCIENTIFIC){
                    ScientificPanel(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        setText = viewModel::setText
                    )
                }
                else{
                    ProgrammerPanel(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        setText = viewModel::setText
                    )
                }
            }

            ControlPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .weight(1f),
                setText = viewModel::setText,
                doBackspace = viewModel::backspace,
                doClear = viewModel::clear,
                isPlain = viewModel.currentMode.value == CalcViewModel.Mode.PLAIN
            )


            Row(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .weight(4f)){

                val modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()

                if(viewModel.currentMode.value != CalcViewModel.Mode.PLAIN){
                    val operators = if(viewModel.currentMode.value ==  CalcViewModel.Mode.SCIENTIFIC)
                        listOf("%", "^", "√", "!") else listOf(">", "<", "≥", "≤")

                    Column(
                        modifier = Modifier
                            .then(modifier)
                            .weight(1f)
                    ) {

                        for(operator in operators){
                            CalcButton(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                text = AnnotatedString(operator) ,
                                onClick = { viewModel.setText(operator) })
                        }

                    }

                }

                Numpad(
                    modifier = Modifier
                        .then(modifier)
                        .weight(3f),
                    numbers = numpadButtons.value,
                    allowedTokens = allowedTokens.value,
                    setText = viewModel::setText
                )

                MainOperatorsPanel(
                    modifier = Modifier
                        .then(modifier)
                        .weight(1f),
                    setText = viewModel::setText,
                    compute = viewModel::compute
                )

            }


        }
    }
}










