package com.example.testtelephoto.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NextScreen(
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
            text = "Back",
            minWidth = 240.dp,
            onClick = onNavigateToDrawing,
            enabled = true
        )
    }
}
