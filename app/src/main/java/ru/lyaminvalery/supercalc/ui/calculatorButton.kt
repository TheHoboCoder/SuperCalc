package ru.lyaminvalery.supercalc.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.lyaminvalery.supercalc.model.ButtonState
import ru.lyaminvalery.supercalc.model.TextButtonState

@Composable
fun CalculatorButton(modifier: Modifier,
                     buttonState: ButtonState,
                     allowedTokens: List<String>,
                     buttonColor: Color? = null,
                     textColor: Color? = null){
    val btnColor = buttonColor ?: if(buttonState is TextButtonState)
                          MaterialTheme.colors.primary else MaterialTheme.colors.primaryVariant
    Button(onClick = buttonState::onClick,
//        enabled = buttonState.enabled(charset),
        modifier = Modifier
            .padding(all = 4.dp)
            .fillMaxHeight()
            .then(modifier),
        colors = ButtonDefaults.buttonColors(backgroundColor = btnColor)) {
        Text(buttonState.caption,
            color = textColor ?: MaterialTheme.colors.onPrimary,
            fontWeight = FontWeight.Bold,
            )
    }
}

@Composable
fun ButtonRow(modifier: Modifier,
              states: List<ButtonState>,
              allowedTokens: List<String>,
              buttonColor: Color? = null,
              textColor: Color? = null){
    Row(modifier){
        for (state in states){
            CalculatorButton(modifier = Modifier.fillMaxWidth().weight(1f),
                buttonState = state,
                allowedTokens = allowedTokens,
                buttonColor = buttonColor,
                textColor = textColor)
        }
    }
}

@Composable
fun ButtonColumn(modifier: Modifier,
                 states: List<ButtonState>,
                 allowedTokens: List<String>,
                 buttonColor: Color? = null,
                 textColor: Color? = null){
    Column(modifier){
        for (state in states){
            CalculatorButton(modifier = Modifier.fillMaxWidth().weight(1f),
                buttonState = state,
                allowedTokens = allowedTokens,
                buttonColor = buttonColor,
                textColor = textColor)
        }
    }
}