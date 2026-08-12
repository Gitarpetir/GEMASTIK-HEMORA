package com.gemastik.hemora.presentation.dashboard_uks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DashboardUksScreen(
    viewModel: DashboardUksViewModel,
    onNavigateToSchedule: () -> Unit,
    onNavigateToSchoolCode: () -> Unit,
    onNavigateToStudents: () -> Unit,
    onNavigateToSchoolStatistics: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DashboardUksContent(
        uiState = uiState,
        onNavigateToSchedule = onNavigateToSchedule,
        onNavigateToSchoolCode = onNavigateToSchoolCode,
        onNavigateToStudents = onNavigateToStudents,
        onNavigateToSchoolStatistics = onNavigateToSchoolStatistics,
        onLogoutClick = { viewModel.logout(onNavigateToLogin) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardHeader(
    schoolName: String,
    schoolCode: String,
    onNavigateToSchoolCode: () -> Unit,
    onLogoutClick: () -> Unit
) {
    TopAppBar(
        title = { 
            Text(schoolName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        },
        actions = {
            IconButton(onClick = onNavigateToSchoolCode) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Pengaturan Sekolah"
                )
            }
            IconButton(onClick = onLogoutClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Keluar"
                )
            }
        }
    )
}

@Composable
fun DashboardUksContent(
    uiState: DashboardUksUiState,
    onNavigateToSchedule: () -> Unit,
    onNavigateToSchoolCode: () -> Unit,
    onNavigateToStudents: () -> Unit,
    onNavigateToSchoolStatistics: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            if (uiState is DashboardUksUiState.Success) {
                DashboardHeader(
                    schoolName = uiState.schoolName,
                    schoolCode = uiState.schoolCode,
                    onNavigateToSchoolCode = onNavigateToSchoolCode,
                    onLogoutClick = onLogoutClick
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is DashboardUksUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is DashboardUksUiState.Error -> {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is DashboardUksUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(24.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "School Code",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = uiState.schoolCode,
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(uiState.schoolCode))
                                    }
                                ) {
                                    Text("Salin Kode")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Berikan School Code ini kepada Siswi Remaja Putri untuk mendaftar.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = onNavigateToSchedule,
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text("Kelola Jadwal TTD")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onNavigateToStudents,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("Lihat Daftar Siswi")
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onNavigateToSchoolStatistics,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                        ) {
                            Text("Statistik Kepatuhan Sekolah")
                        }
                    }
                }
            }
        }
    }
}
