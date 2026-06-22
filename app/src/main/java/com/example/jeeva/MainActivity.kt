package com.example.jeeva

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.jeeva.navigation.AppNavigation
import com.example.jeeva.ui.theme.JeevaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enabling edge-to-edge can sometimes cause the UI to be drawn under status bars,
        // but it shouldn't cause a completely black screen unless themes are wrong.
        enableEdgeToEdge()
        
        setContent {
            JeevaTheme {
                // We'll use a Surface as the root to ensure we have a background color
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White // Forcing White background to troubleshoot black screen
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
