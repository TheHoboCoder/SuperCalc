package ru.lyaminvalery.supercalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.lyaminvalery.supercalc.ui.ButtonColumn
import ru.lyaminvalery.supercalc.ui.ButtonRow
import ru.lyaminvalery.supercalc.ui.Numpad
import ru.lyaminvalery.supercalc.ui.TopPane
import ru.lyaminvalery.supercalc.ui.theme.SuperCalcTheme
import ru.lyaminvalery.supercalc.viewmodel.CalcViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CalcViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperCalcTheme {
                Scaffold(
                    topBar = {
                        TopAppBar {
                            IconButton(onClick = { }) {Icon(Icons.Filled.Menu,
                                contentDescription = getString(R.string.menu)) }
                            Text(getString(R.string.app_name), fontSize = 22.sp)
                        }
                    }
                ) {
                        padding -> MainStack(padding)
                }
            }
        }
    }


    @Composable
    fun MainStack(padding: PaddingValues){

        Column(modifier= Modifier
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
            val currentMode = remember { viewModel.currentMode }

            Row(
                Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(8.dp)){

                ClickableText(modifier = Modifier.padding(end=8.dp),
                    text = AnnotatedString(getString(R.string.mode_plain)),
                    style = if(currentMode.value == CalcViewModel.Mode.PLAIN)
                        TextStyle(MaterialTheme.colors.onPrimary, fontWeight = FontWeight.Bold)
                            else TextStyle(MaterialTheme.colors.onPrimary, fontWeight = FontWeight.Normal),
                    onClick = { viewModel.changeMode(CalcViewModel.Mode.PLAIN) })

                ClickableText(modifier = Modifier.padding(end=8.dp),
                    text = AnnotatedString(getString(R.string.mode_scientific)),
                    style = if(currentMode.value == CalcViewModel.Mode.PLAIN)
                        TextStyle(MaterialTheme.colors.onPrimary, fontWeight = FontWeight.Bold)
                    else TextStyle(MaterialTheme.colors.onPrimary, fontWeight = FontWeight.Normal),
                    onClick = { viewModel.changeMode(CalcViewModel.Mode.SCIENTIFIC) })

                ClickableText(modifier = Modifier.padding(end=8.dp),
                    text = AnnotatedString(getString(R.string.mode_programmer)),
                    style = if(currentMode.value == CalcViewModel.Mode.PLAIN)
                        TextStyle(MaterialTheme.colors.onPrimary, fontWeight = FontWeight.Bold)
                    else TextStyle(MaterialTheme.colors.onPrimary, fontWeight = FontWeight.Normal),
                    onClick = { viewModel.changeMode(CalcViewModel.Mode.PROGRAMMER) })
            }

            TopPane(modifier = Modifier
                .fillMaxHeight()
                .weight(2f),
                    inputText = inputVal.value,
                    outputText = outputVal.value,
                    hasError = isFailed.value,
                    textPosition = inputPosition.value,
                    setTextPosition = viewModel::setInputPosition)

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










