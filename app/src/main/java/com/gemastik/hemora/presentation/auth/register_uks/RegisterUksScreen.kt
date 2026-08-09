package com.gemastik.hemora.presentation.auth.register_uks

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun RegisterUksScreen(
    viewModel: RegisterUksViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    RegisterUksContent(
        uiState = uiState,
        onRegisterClick = { name, email, pass, school, code ->
            viewModel.register(name, email, pass, school, code)
        },
        onNavigateToLogin = onNavigateToLogin,
        onRegisterSuccess = onRegisterSuccess
    )
}

@Composable
fun RegisterUksContent(
    uiState: RegisterUksUiState,
    onRegisterClick: (String, String, String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var schoolName by remember { mutableStateOf("") }
    var activationCode by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is RegisterUksUiState.Success) {
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Daftar UKS",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nama UKS/Admin") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = schoolName,
            onValueChange = { schoolName = it },
            label = { Text("Nama Sekolah") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = activationCode,
            onValueChange = { activationCode = it },
            label = { Text("Kode Aktivasi UKS") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
            singleLine = true
        )

        if (uiState is RegisterUksUiState.Error) {
            Text(
                text = (uiState as RegisterUksUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = { onRegisterClick(name, email, password, schoolName, activationCode) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = uiState !is RegisterUksUiState.Loading
        ) {
            if (uiState is RegisterUksUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Daftar sebagai UKS")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text("Sudah punya akun? Silakan Login")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterUksScreenPreview() {
    MaterialTheme {
        RegisterUksContent(
            uiState = RegisterUksUiState.Idle,
            onRegisterClick = { _, _, _, _, _ -> },
            onNavigateToLogin = {},
            onRegisterSuccess = {}
        )
    }
}
