package com.gemastik.hemora.presentation.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gemastik.hemora.domain.schedule.model.TtdSchedule

@Composable
fun ScheduleScreen(
    viewModel: ScheduleViewModel,
    onNavigateToAddEdit: (String?, String?, String?) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ScheduleContent(
        uiState = uiState,
        onAddClick = { onNavigateToAddEdit(null, null, null) },
        onEditClick = { schedule -> onNavigateToAddEdit(schedule.scheduleId, schedule.date, schedule.time) },
        onDeleteClick = { scheduleId -> viewModel.deleteSchedule(scheduleId) }
    )
}

@Composable
fun ScheduleContent(
    uiState: ScheduleUiState,
    onAddClick: () -> Unit,
    onEditClick: (TtdSchedule) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Jadwal")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when (uiState) {
                is ScheduleUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ScheduleUiState.Error -> {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ScheduleUiState.Success -> {
                    val schedules = uiState.schedules
                    if (schedules.isEmpty()) {
                        Text(
                            text = "Belum ada jadwal TTD. Silakan tambah jadwal baru.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                Text(
                                    text = "Jadwal Konsumsi TTD",
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                            }
                            items(schedules) { schedule ->
                                ScheduleItemCard(
                                    schedule = schedule,
                                    onEdit = { onEditClick(schedule) },
                                    onDelete = { onDeleteClick(schedule.scheduleId) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleItemCard(
    schedule: TtdSchedule,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Tanggal: ${schedule.date}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Jam: ${schedule.time}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
