package ru.lyaminvalery.supercalc.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString

@Composable
fun Numpad(modifier: Modifier,
           numbers: List<String>,
           allowedTokens: List<String>,
           setText: (String) -> Unit){

    Column(modifier=modifier) {

        val rows = numbers.chunked(3)
            .toMutableList()
            .apply {
                add(0, listOf(", ", "0", "."))
                reverse()
            }

        for(row in rows){

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .weight(1f)
            ){
                for(number in row){

                    CalcButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        text = AnnotatedString(number),
                        onClick = { setText(number)},
                        buttonColor = MaterialTheme.colors.secondary
                    )

                }
            }
        }
    }
}