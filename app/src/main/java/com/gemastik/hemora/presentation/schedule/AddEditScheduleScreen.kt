package com.gemastik.hemora.presentation.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddEditScheduleScreen(
    viewModel: ScheduleViewModel,
    scheduleId: String?,
    initialDate: String?,
    initialTime: String?,
    onNavigateBack: () -> Unit
) {
    var date by remember { mutableStateOf(initialDate ?: "") }
    var time by remember { mutableStateOf(initialTime ?: "") }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = if (scheduleId == null) "Tambah Jadwal" else "Edit Jadwal",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Tanggal (YYYY-MM-DD)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = time,
                onValueChange = { time = it },
                label = { Text("Waktu (HH:MM)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                singleLine = true
            )

            Button(
                onClick = {
                    if (scheduleId == null) {
                        viewModel.addSchedule(date, time)
                    } else {
                        viewModel.updateSchedule(scheduleId, date, time)
                    }
                    onNavigateBack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = date.isNotBlank() && time.isNotBlank()
            ) {
                Text("Simpan")
            }
        }
    }
}
