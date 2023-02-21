package ru.lyaminvalery.supercalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.lyaminvalery.supercalc.ui.theme.SuperCalcTheme
import ru.lyaminvalery.supercalc.viewmodel.CalcViewModel

class MainActivity : ComponentActivity() {

    private val viewModel = CalcViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperCalcTheme {
                mainStack()
            }
        }
    }

    fun backspace(){

    }

    fun changeSign(){

    }


    @Composable
    fun mainStack(){
        Column(modifier= Modifier
            .padding(all = 8.dp)
            .fillMaxSize()) {

            CompositionLocalProvider(
                LocalTextInputService provides null
            ){
                val inputVal = remember { viewModel.inputText}
                TextField(
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .weight(2f),
                    textStyle = TextStyle(fontSize=30.sp),
                    value = inputVal.value,
                    onValueChange = {})
            }

            val outputVal = remember { viewModel.outputText}
            val isFailed = remember { viewModel.isFailed}
            Text(outputVal.value,
                fontSize = 20.sp,
                color = if (!isFailed.value) Color.Black else Color.Red,
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth())

            Column(modifier = Modifier
                .fillMaxHeight()
                .weight(3f)){

                Row(modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)){
                    Button(onClick = { viewModel.clear() },
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .fillMaxWidth()
                            .weight(1f)
                            .fillMaxHeight(),) {
                        Text("C", fontWeight = FontWeight.Bold)
                    }

                    Button(onClick = { backspace() },
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .fillMaxWidth()
                            .weight(1f)
                            .fillMaxHeight(),) {
                        Icon(Icons.Sharp.Delete, contentDescription = "TODO")
                    }

                    Button(onClick = { viewModel.setText('(') },
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .fillMaxWidth()
                            .weight(1f)
                            .fillMaxHeight(),) {
                        Text("(", fontWeight = FontWeight.Bold)
                    }

                    Button(onClick = { viewModel.setText(')') },
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .fillMaxWidth()
                            .weight(1f)
                            .fillMaxHeight(),) {
                        Text(")", fontWeight = FontWeight.Bold)
                    }
                }

                keyBoardRow(start = 7, end = 9, operation = '*',
                    Modifier
                        .fillMaxHeight()
                        .weight(1f))
                keyBoardRow(start = 4, end = 6, operation = '-',
                    Modifier
                        .fillMaxHeight()
                        .weight(1f))
                keyBoardRow(start = 1, end = 3, operation = '+',
                    Modifier
                        .fillMaxHeight()
                        .weight(1f))

                Row(modifier= Modifier
                    .fillMaxHeight()
                    .weight(1f)){
                    Button(onClick = {  changeSign()  },
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .fillMaxWidth()
                            .weight(1f)
                            .fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.hsl(250.0F, .5F, .6F))) {
                        Text("+/-", color = Color.White)
                    }

                    Button(onClick = { viewModel.setText('0') },
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .fillMaxWidth()
                            .weight(1f)
                            .fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.hsl(250.0F, .5F, .6F))) {
                        Text("0", color = Color.White)
                    }

                    Button(onClick = {  viewModel.setText(',') },
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .fillMaxWidth()
                            .weight(1f)
                            .fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.hsl(250.0F, .5F, .6F))) {
                        Text(",", color = Color.White)
                    }
                    Button(onClick = { viewModel.compute() },
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .fillMaxWidth()
                            .weight(1f)
                            .fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.hsl(269.0F, .68F, .21F))) {
                        Text("=", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                }
            }

        }
    }

    @Composable
    fun keyBoardRow(start: Int, end: Int, operation: Char, modifier: Modifier){

        Row(modifier=modifier){
            for(i in start..end){
                Button(onClick = {  viewModel.setText(i.digitToChar()) },
                    modifier= Modifier
                        .padding(all = 8.dp)
                        .fillMaxWidth()
                        .weight(1f)
                        .fillMaxHeight(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.hsl(250.0F, .5F, .6F))) {
                    Text(i.toString(), color = Color.White)
                }
            }

            Button(onClick = {  viewModel.setText(operation) },
                modifier= Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth()
                    .weight(1f)
                    .fillMaxHeight()) {
                Text(operation.toString(), fontWeight = FontWeight.Bold)
            }

        }
    }

    @Preview
    @Composable
    fun preview(){
        mainStack()
    }

}










