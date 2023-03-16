package ru.lyaminvalery.supercalc.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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