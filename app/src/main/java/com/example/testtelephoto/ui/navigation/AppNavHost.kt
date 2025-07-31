package com.example.testtelephoto.ui.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.testtelephoto.ui.screens.DrawingScreen


@SuppressLint("WrongNavigateRouteType")
@Composable
fun AppNavHost(
    modifier: Modifier,
    navController: NavHostController,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Drawing
    ) {
        composable<Drawing> {
            DrawingScreen()
        }
    }
}