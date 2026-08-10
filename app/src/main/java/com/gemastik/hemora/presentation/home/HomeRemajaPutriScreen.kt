package com.gemastik.hemora.presentation.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
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
import com.gemastik.hemora.presentation.consumption.history.HistoryScreen
import com.gemastik.hemora.presentation.consumption.tracker.TrackerScreen

@Composable
fun HomeRemajaPutriScreen() {
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    val items = listOf("Beranda", "Riwayat")

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            if (index == 0) Icon(Icons.Filled.Home, contentDescription = item)
                            else Icon(Icons.Filled.List, contentDescription = item)
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
            }
        }
    }
}
