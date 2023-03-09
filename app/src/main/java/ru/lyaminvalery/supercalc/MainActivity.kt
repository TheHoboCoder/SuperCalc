package ru.lyaminvalery.supercalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalTextInputService
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
                MainStack()
            }
        }
    }

    @Composable
    fun TopPane(modifier: Modifier,
                inputText: String,
                outputText: String,
                hasError: Boolean,
                viewModel: CalcViewModel){
        Column(modifier=modifier){
            CompositionLocalProvider(
                LocalTextInputService provides null
            ){

                //val textState = remember { mutableStateOf(TextFieldValue(inputText)) }
                TextField(
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .fillMaxSize()
                        .weight(2f),

                    textStyle = TextStyle(fontSize=30.sp, textAlign = TextAlign.End),
                    value = TextFieldValue(inputText),
                    onValueChange = {
                        viewModel.currentPos = it.selection.start
                        //textState.value = it
                    })
            }

            Text(outputText,
                fontSize = 20.sp,
                textAlign = TextAlign.End,
                color = if (!hasError) Color.Black else Color.Red,
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxSize()
                    .weight(1f))
        }
    }

    @Composable
    fun ControlPane(modifier: Modifier,  viewModel: CalcViewModel){

        Row(modifier = modifier) {
            Button(
                onClick = { viewModel.setText("(") },
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth()
                    .weight(1f)
                    .aspectRatio(1f),
            ) {
                Text("(", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { viewModel.setText(")") },
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth()
                    .weight(1f)
                    .aspectRatio(1f),
            ) {
                Text(")", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { viewModel.clear() },
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth()
                    .weight(1f)
                    .aspectRatio(1f),
            ) {
                Text("C", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { viewModel.backspace() },
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth()
                    .weight(1f)
                    .aspectRatio(1f),
            ) {
                Icon(Icons.Sharp.Delete, contentDescription = "TODO")
            }
        }
    }

    @Composable
    fun Numpad(modifier: Modifier,  viewModel: CalcViewModel){
        Column(modifier=modifier) {
            val rows = arrayOf(('7'..'9').toList(),
                                ('4'..'6').toList(),
                                ('1'..'3').toList(),
                                listOf('#', '0', '.'))
            for(row in rows){
                Row(modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)) {

                    for(label in row){
                        Button(onClick = {
                                if (label != '#'){
                                    viewModel.setText(label.toString())
                                }
                                else{
                                    viewModel.changeMode()
                                }
                             },
                            modifier = Modifier
                                .padding(all = 8.dp)
                                .fillMaxWidth()
                                .weight(1f)
                                .fillMaxHeight(),
                            colors = ButtonDefaults.buttonColors(backgroundColor = textBtnColor)) {
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
                      viewModel: CalcViewModel){
        Column(modifier) {
            for (row in operationList){
                Row(Modifier.fillMaxHeight().weight(1f)){
                    for(operation in row){
                        Button(onClick = {  viewModel.setText(operation) },
                            modifier= Modifier
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
                       viewModel: CalcViewModel){
        Row(modifier){
            for(operation in operationList){
                Button(onClick = {  viewModel.setText(operation) },
                    modifier= Modifier
                        .padding(all = 8.dp)
                        .fillMaxWidth()
                        .weight(1f)
                        .aspectRatio(1f)) {
                    Text(operation, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    @Composable
    fun MainStack(){
        Column(modifier= Modifier
            .padding(all = 8.dp)
            .fillMaxSize()) {

            val inputVal = remember { viewModel.inputText}
            val outputVal = remember { viewModel.outputText}
            val isFailed = remember { viewModel.isFailed}

            TopPane(modifier = Modifier
                .fillMaxHeight()
                .weight(2f),
                    inputText = inputVal.value,
                    outputText = outputVal.value,
                    hasError = isFailed.value,
                    viewModel = viewModel)

            ControlPane(modifier = Modifier.fillMaxWidth(), viewModel = viewModel)

            val addOperationList = remember { viewModel.addOperationList}

            if(addOperationList.value.isNotEmpty()){
                AdditionalPane(modifier = Modifier.fillMaxWidth(),
                    operationList = addOperationList.value,
                    viewModel = viewModel)
            }

            Row(modifier = Modifier
                .fillMaxHeight()
                .weight(3f)){

                Numpad(modifier = Modifier
                    .fillMaxWidth()
                    .weight(3f), viewModel = viewModel)

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)){

                    OperationPane(modifier = Modifier
                        .fillMaxHeight()
                        .weight(3f),
                        viewModel = viewModel,
                        operationList = listOf(listOf("^", "√"), listOf("*", "/"), listOf("+", "-"))
                    )

                    Button(onClick = {  viewModel.compute() },
                            modifier= Modifier
                                .padding(all = 8.dp)
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .weight(1f)) {
                            Text("=", fontWeight = FontWeight.Bold)
                        }

                }

            }


        }
    }


    @Preview
    @Composable
    fun Preview(){
        MainStack()
    }

}










