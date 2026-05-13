package com.example.jeeva.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jeeva.ui.screens.*
import com.example.jeeva.ui.viewmodel.BloodRequestViewModel
import com.example.jeeva.ui.viewmodel.DonorViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val donorViewModel: DonorViewModel = viewModel()
    val requestViewModel: BloodRequestViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable(route = "splash") {
            SplashScreen(navController)
        }

        composable(route = "login") {
            LoginScreen(
                navController = navController,
                viewModel = donorViewModel
            )
        }

        composable(route = "main") {
            MainScreen(
                donorViewModel = donorViewModel,
                requestViewModel = requestViewModel,
                onNavigateToRegister = { navController.navigate("register_donor") },
                onNavigateToRequest = { navController.navigate("request_blood") }
            )
        }

        composable(route = "register_donor") {
            RegisterDonorScreen(
                viewModel = donorViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(route = "request_blood") {
            RequestBloodScreen(
                viewModel = requestViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
