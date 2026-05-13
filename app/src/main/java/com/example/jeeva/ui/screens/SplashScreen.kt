package com.example.jeeva.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.jeeva.ui.theme.RedPrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {

    LaunchedEffect(Unit) {

        delay(2000)

        navController.navigate("login")
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "🩸",
                fontSize = 80.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Jeeva-Bindu",
                fontSize = 30.sp,
                color = RedPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Rapid Blood Response System"
            )
        }
    }
}