package com.example.jeeva.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColors = lightColorScheme(
    primary = RedPrimary,
    secondary = GreenPrimary
)

@Composable
fun JeevaTheme(content: @Composable () -> Unit) {

    MaterialTheme(
        colorScheme = AppColors,
        content = content
    )
}