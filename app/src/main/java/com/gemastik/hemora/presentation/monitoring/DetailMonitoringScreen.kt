package com.gemastik.hemora.presentation.monitoring

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gemastik.hemora.core.utils.ViewModelFactory
import com.gemastik.hemora.domain.model.ConsumptionStatus
import com.gemastik.hemora.domain.monitoring.model.StudentMonitoringItem
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailMonitoringScreen(
    navController: NavController,
    studentId: String,
    studentName: String,
    viewModel: DetailMonitoringViewModel = viewModel(factory = ViewModelFactory.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(studentId) {
        viewModel.loadStudentData(studentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Monitoring") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = studentName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            when (val state = uiState) {
                is DetailMonitoringState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is DetailMonitoringState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is DetailMonitoringState.Success -> {
                    if (state.items.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "Belum ada jadwal TTD dari sekolah.")
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.items) { item ->
                                MonitoringItemCard(item)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MonitoringItemCard(item: StudentMonitoringItem) {
    val scheduleFormat = SimpleDateFormat("dd MMMM yyyy", Locale.forLanguageTag("id-ID"))
    val scheduleDateStr = scheduleFormat.format(item.schedule.date)
    
    val isConsumed = item.consumption?.status == ConsumptionStatus.SUDAH_KONSUMSI
    val cardColor = if (isConsumed) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
    val icon = if (isConsumed) Icons.Default.CheckCircle else Icons.Default.Warning
    val iconTint = if (isConsumed) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
    val statusText = if (isConsumed) "Sudah Konsumsi" else "Belum Konsumsi"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Jadwal Minum TTD",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Jadwal: $scheduleDateStr",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelLarge,
                    color = iconTint,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
