package com.example.jeeva.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.jeeva.ui.viewmodel.BloodRequestViewModel
import com.example.jeeva.ui.viewmodel.DonorViewModel

@Composable
fun MainScreen(
    donorViewModel: DonorViewModel,
    requestViewModel: BloodRequestViewModel,
    onNavigateToRegister: () -> Unit,
    onNavigateToRequest: () -> Unit
) {
    val navController = rememberNavController()
    
    val items = listOf(
        BottomNavItem("Home", "home_tab", Icons.Default.Home),
        BottomNavItem("Donors", "donors_tab", Icons.Default.Favorite),
        BottomNavItem("Requests", "requests_tab", Icons.Default.Opacity),
        BottomNavItem("Profile", "profile_tab", Icons.Default.Person)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = com.example.jeeva.ui.theme.RedPrimary,
                            selectedTextColor = com.example.jeeva.ui.theme.RedPrimary,
                            indicatorColor = com.example.jeeva.ui.theme.RedPrimary.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home_tab",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home_tab") {
                HomeScreen(
                    donorViewModel = donorViewModel,
                    requestViewModel = requestViewModel,
                    onNavigateToRegister = onNavigateToRegister,
                    onNavigateToRequest = onNavigateToRequest,
                    onNavigateToAllDonors = { navController.navigate("donors_tab") },
                    onNavigateToAllRequests = { navController.navigate("requests_tab") }
                )
            }
            composable("donors_tab") {
                DonorsScreen(viewModel = donorViewModel)
            }
            composable("requests_tab") {
                RequestsScreen(viewModel = requestViewModel)
            }
            composable("profile_tab") {
                ProfileScreen(viewModel = donorViewModel)
            }
        }
    }
}

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
