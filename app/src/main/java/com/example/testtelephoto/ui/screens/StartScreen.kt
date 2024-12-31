package com.example.testtelephoto.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StartScreen(
    onNavigateToDrawing: () -> Unit,
) {
    val topBarColor = MaterialTheme.colorScheme.surface
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(topBarColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        DefaultButton(
            text = "Start",
            minWidth = 240.dp,
            onClick = onNavigateToDrawing,
            enabled = true
        )
    }
}

@Composable
fun DefaultButton(text: String, minWidth: Dp, enabled: Boolean, onClick: () -> Unit) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val contentColor = MaterialTheme.colorScheme.outline
    var isButtonEnabled by remember { mutableStateOf(enabled) }
    LaunchedEffect(enabled) {
        isButtonEnabled = enabled
    }

    Button(
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        onClick = {
            isButtonEnabled = false
            onClick()
        },
        enabled = enabled,
        shape = CircleShape,
        border = BorderStroke(2.dp, contentColor),
        modifier = Modifier
            .widthIn(min = minWidth)
    ) {
        Text(
            text = text,
            fontSize = 25.sp,
            color = contentColor
        )
    }
}