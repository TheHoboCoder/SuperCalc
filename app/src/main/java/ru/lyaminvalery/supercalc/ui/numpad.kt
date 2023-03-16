package ru.lyaminvalery.supercalc.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ru.lyaminvalery.supercalc.model.ButtonState
import ru.lyaminvalery.supercalc.model.TextButtonState

@Composable
fun Numpad(modifier: Modifier,
           buttons: List<ButtonState>,
           allowedTokens: List<String>,
           setText: (String) -> Unit){

    Column(modifier=modifier) {

        val rows = buttons.chunked(3)
            .toMutableList()
            .apply {
                add(0, listOf(TextButtonState(",", setText, ", "),
                    TextButtonState("0", setText),
                    TextButtonState(".", setText)))
                reverse()
            }

        for(row in rows){

            ButtonRow(modifier = Modifier
                .fillMaxHeight()
                .weight(1f),
                states = row,
                allowedTokens = allowedTokens, MaterialTheme.colors.secondary, Color.White
            )
        }
    }
}