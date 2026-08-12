package com.gemastik.hemora.presentation.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gemastik.hemora.core.utils.ViewModelFactory
import com.gemastik.hemora.presentation.consumption.history.HistoryScreen
import com.gemastik.hemora.presentation.consumption.tracker.TrackerScreen
import com.gemastik.hemora.presentation.profile.ProfileScreen
import com.gemastik.hemora.presentation.profile.ProfileViewModel

@Composable
fun HomeRemajaPutriScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToEducationDetail: (String) -> Unit
) {
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    val items = listOf("Beranda", "Riwayat", "Statistik", "Edukasi", "Profil")

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            when (index) {
                                0 -> Icon(Icons.Filled.Home, contentDescription = item)
                                1 -> Icon(Icons.AutoMirrored.Filled.List, contentDescription = item)
                                2 -> Icon(Icons.Filled.Info, contentDescription = item)
                                3 -> Icon(Icons.Filled.Star, contentDescription = item)
                                4 -> Icon(Icons.Filled.Person, contentDescription = item)
                            }
                        },
                        label = { Text(item) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
                    )
                }
            }
        }
    ) { innerPadding ->
        androidx.compose.foundation.layout.Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedItem) {
                0 -> TrackerScreen()
                1 -> HistoryScreen()
                2 -> com.gemastik.hemora.presentation.consumption.statistics.StatisticsScreen()
                3 -> {
                    val educationViewModel: com.gemastik.hemora.presentation.education.EducationViewModel = viewModel(factory = ViewModelFactory.Factory)
                    com.gemastik.hemora.presentation.education.EducationListScreen(
                        viewModel = educationViewModel,
                        onNavigateToDetail = onNavigateToEducationDetail
                    )
                }
                4 -> {
                    val profileViewModel: ProfileViewModel = viewModel(factory = ViewModelFactory.Factory)
                    ProfileScreen(viewModel = profileViewModel, onNavigateToLogin = onNavigateToLogin)
                }
            }
        }
    }
}
