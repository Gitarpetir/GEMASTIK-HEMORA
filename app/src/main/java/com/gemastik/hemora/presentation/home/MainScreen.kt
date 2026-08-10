package com.gemastik.hemora.presentation.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gemastik.hemora.presentation.consumption.history.HistoryScreen
import com.gemastik.hemora.presentation.consumption.statistics.StatisticsScreen
import com.gemastik.hemora.presentation.consumption.tracker.TrackerScreen

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Tracker : BottomNavItem("tracker", "Beranda", Icons.Default.Home)
    object History : BottomNavItem("history", "Riwayat", Icons.AutoMirrored.Filled.List)
    object Statistics : BottomNavItem("statistics", "Statistik", Icons.Default.Star)
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    
    val items = listOf(
        BottomNavItem.Tracker,
        BottomNavItem.History,
        BottomNavItem.Statistics
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Tracker.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Tracker.route) { TrackerScreen() }
            composable(BottomNavItem.History.route) { HistoryScreen() }
            composable(BottomNavItem.Statistics.route) { StatisticsScreen() }
        }
    }
}
