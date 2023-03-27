package ru.lyaminvalery.supercalc.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CalcButton(modifier: Modifier,
               text: AnnotatedString,
               onClick: () -> Unit,
               isControl: Boolean = false,
               buttonColor: Color? = null,
               textColor: Color? = null
){
    val btnColor = buttonColor ?: if(!isControl)
        MaterialTheme.colors.primary else MaterialTheme.colors.primaryVariant

    Button(onClick = onClick,
        modifier = Modifier
            .padding(all = 4.dp)
            .fillMaxHeight()
            .then(modifier),
        colors = ButtonDefaults.buttonColors(backgroundColor = btnColor)) {
        Text(
            text,
            color = textColor ?: MaterialTheme.colors.onPrimary,
            fontWeight = FontWeight.Bold,
        )
    }

}

@Composable
fun DropDown(modifier: Modifier,
             functions: List<String>,
             operators: List<String>,
             setText: (String) -> Unit){

    val expanded = remember {
        mutableStateOf(false)
    }

    Box(modifier = modifier){

        CalcButton(
            modifier = modifier,
            text = AnnotatedString("..."),
            onClick = { expanded.value = !expanded.value },
            isControl = true
        )

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
        ) {
            // 6
            for(func in functions){
                DropdownMenuItem(onClick = { setText("$func(") }) {
                    Text(func)
                }
            }

            for(operator in operators){
                DropdownMenuItem(onClick = { setText(operator) }) {
                    Text(operator)
                }
            }
        }
    }

}

