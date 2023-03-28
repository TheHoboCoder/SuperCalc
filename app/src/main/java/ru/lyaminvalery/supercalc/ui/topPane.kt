package ru.lyaminvalery.supercalc.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TopPane(modifier: Modifier,
            inputText: String,
            outputText: String,
            hasError: Boolean,
            textPosition: Int,
            setTextPosition: (Int) -> Unit,
            includeRadix: Boolean = false,
            radix: Int,
            setRadix: (String) -> Unit,
            isRadixValid: Boolean){
    Column(modifier=modifier){
        CompositionLocalProvider(
            LocalTextInputService provides null
        ){

            TextField(
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth()
                    .weight(1f),

                textStyle = TextStyle(fontSize=30.sp, textAlign = TextAlign.End),
                value = TextFieldValue(inputText, TextRange(textPosition)),
                onValueChange = {
                    setTextPosition(it.selection.start)
                })
        }

        Row(modifier = Modifier
            .padding(all = 8.dp)
            .fillMaxWidth()
            .heightIn(min=40.dp),
            verticalAlignment = Alignment.CenterVertically){

            Text(outputText,
                modifier = Modifier.fillMaxWidth().weight(3f),
                fontSize = 20.sp,
                textAlign = TextAlign.End,
                color = if (!hasError) MaterialTheme.colors.onSurface else MaterialTheme.colors.onError,
                )


            if(includeRadix){
                val radixTextValue = remember {
                    mutableStateOf(TextFieldValue(radix.toString()))
                }
                val keyboardController = LocalSoftwareKeyboardController.current
                TextField(modifier = Modifier.wrapContentHeight(Alignment.CenterVertically)
                    .width(80.dp)
                    .padding(start = 16.dp),
                    value = radixTextValue.value,
                    textStyle = TextStyle(textAlign = TextAlign.End),
                    colors = TextFieldDefaults.textFieldColors(textColor =
                                if(isRadixValid) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onError),
                    onValueChange = {
                        radixTextValue.value = it
                        if(it.text != ""){
                            setRadix(it.text)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {keyboardController?.hide()})
                )

                val menuExpanded = remember {
                    mutableStateOf(false)
                }

                Box(
                ){
                    IconButton(onClick = { menuExpanded.value = true}) {
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = "t")
                    }

                    DropdownMenu(
                        expanded = menuExpanded.value,
                        onDismissRequest = { menuExpanded.value = false },
                    ) {

                        for(i in listOf("10", "2", "8", "16")){
                            DropdownMenuItem(
                                onClick = {
                                    menuExpanded.value = false
                                    radixTextValue.value = TextFieldValue(i)
                                    setRadix(i)
                                },
                            ){
                                Text(i)
                            }
                        }

                    }
                }

            }
        }


    }
}