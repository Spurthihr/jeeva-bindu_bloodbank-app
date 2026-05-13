package com.example.jeeva

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.jeeva.ui.screens.SplashScreen
import com.example.jeeva.ui.screens.LoginScreen
import com.example.jeeva.ui.screens.HomeScreen
import com.example.jeeva.navigation.AppNavigation
import com.example.jeeva.ui.theme.JeevaTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContent {

            JeevaTheme {

                AppNavigation()

            }
        }
    }
}