package ru.lyaminvalery.supercalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.sharp.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.lyaminvalery.supercalc.ui.theme.SuperCalcTheme
import ru.lyaminvalery.supercalc.ui.theme.textBtnColor
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
    fun TopPane(modifier: Modifier,
                inputText: String,
                outputText: String,
                hasError: Boolean,
                textPosition: Int,
                setTextPosition: (Int) -> Unit){
        Column(modifier=modifier){
            CompositionLocalProvider(
                LocalTextInputService provides null
            ){

                TextField(
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .fillMaxSize()
                        .weight(2f),

                    textStyle = TextStyle(fontSize=30.sp, textAlign = TextAlign.End),
                    value = TextFieldValue(inputText, TextRange(textPosition)),
                    onValueChange = {
                        setTextPosition(it.selection.start)
                    })
            }

            Text(outputText,
                fontSize = 20.sp,
                textAlign = TextAlign.End,
                color = if (!hasError) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onError,
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxSize()
                    .weight(1f))
        }
    }

    @Composable
    fun ControlPane(modifier: Modifier,
                    setText: (String) -> Unit,
                    clear: () -> Unit,
                    backspace: () -> Unit,
                    changeMode: () -> Unit){

        Row(modifier = modifier) {
            Button(
                onClick = { setText("(") },
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth()
                    .weight(1f)
                    .fillMaxHeight(),
            ) {
                Text("(", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { setText(")") },
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth()
                    .weight(1f)
                    .fillMaxHeight(),
            ) {
                Text(")", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = changeMode,
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth()
                    .weight(1f)
                    .fillMaxHeight(),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primaryVariant)
            ) {
                Text("...", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = clear,
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth()
                    .weight(1f)
                    .fillMaxHeight(),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primaryVariant)
            ) {
                Text("C", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = backspace,
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth()
                    .weight(1f)
                    .fillMaxHeight(),
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primaryVariant)
            ) {
                Icon(Icons.Sharp.Delete, contentDescription = "TODO")
            }
        }
    }

    @Composable
    fun Numpad(modifier: Modifier,
               charset: List<Char>,
               setText: (String) -> Unit){

        Column(modifier=modifier) {
            val rows = charset.chunked(3)
                              .toMutableList()
                              .apply {
                                   add(0, listOf(',', '0', '.'))
                                   reverse()
                              }

            for(row in rows){
                Row(modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)) {

                    for(label in row){
                        Button(onClick = {setText(label.toString())},
                            modifier = Modifier
                                .padding(all = 8.dp)
                                .fillMaxWidth()
                                .weight(1f)
                                .fillMaxHeight(),
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary)) {
                            Text(label.toString(), color = Color.White)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun OperationPane(modifier: Modifier,
                      operationList: List<List<String>>,
                      setText: (String) -> Unit){
        Column(modifier) {
            for (row in operationList){
                Row(
                    Modifier
                        .fillMaxHeight()
                        .weight(1f)){
                    for(operation in row){
                        Button(onClick = {  setText(operation) },
                            modifier = Modifier
                                .padding(all = 8.dp)
                                .fillMaxWidth()
                                .weight(1f)
                                .fillMaxHeight()) {
                            Text(operation, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun AdditionalPane(modifier: Modifier,
                       operationList: List<String>,
                       setText: (String) -> Unit){
        val buttonsize = LocalConfiguration.current.screenWidthDp / 5
        Row(modifier.horizontalScroll(rememberScrollState())){
            for(operation in operationList){
                Button(onClick = {  setText(operation) },
                    modifier= Modifier
                        .padding(all = 8.dp)
                        .width(buttonsize.dp)
                        .fillMaxHeight()) {
                    Text(operation, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    @Composable
    fun MainStack(padding: PaddingValues){

        Column(modifier= Modifier
            .padding(padding)
            .fillMaxSize()) {

            val inputVal = remember { viewModel.inputText}
            val outputVal = remember { viewModel.outputText}
            val inputPosition = remember { viewModel.currentPos}
            val isFailed = remember { viewModel.isFailed}
            val charset = remember {viewModel.charset }

            TopPane(modifier = Modifier
                .fillMaxHeight()
                .weight(2f),
                    inputText = inputVal.value,
                    outputText = outputVal.value,
                    hasError = isFailed.value,
                    textPosition = inputPosition.value,
                    setTextPosition = viewModel::setInputPosition)

            val addOperationList = remember { viewModel.addOperationList}

            if(addOperationList.value.isNotEmpty()){
                AdditionalPane(modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
                    operationList = addOperationList.value,
                    viewModel::setText)
            }

            ControlPane(modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
                viewModel::setText,
                viewModel::clear,
                viewModel::backspace,
                viewModel::changeMode)


            Box(modifier = Modifier
                .fillMaxHeight()
                .weight(4f)){

                Row(modifier = Modifier
                    .fillMaxHeight()){

                    Numpad(modifier = Modifier
                        .fillMaxWidth()
                        .weight(3f), charset.value, viewModel::setText)

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .weight(2f)){

                        OperationPane(modifier = Modifier
                            .fillMaxHeight()
                            .weight(3f),
                            setText = viewModel::setText,
                            operationList = listOf(listOf("^", "√"), listOf("*", "/"), listOf("+", "-"))
                        )

                        Button(onClick = {  viewModel.compute() },
                            modifier= Modifier
                                .padding(all = 8.dp)
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .weight(1f),
                            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primaryVariant)
                        ) {
                            Text("=", fontWeight = FontWeight.Bold)
                        }

                    }

                }

//                if(addOperationList.value.isNotEmpty()) Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(
//                    Color.Magenta))
            }




        }
    }
}










