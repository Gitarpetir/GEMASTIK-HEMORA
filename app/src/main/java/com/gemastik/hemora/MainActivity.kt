package com.gemastik.hemora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gemastik.hemora.core.utils.ViewModelFactory
import com.gemastik.hemora.presentation.auth.login.LoginScreen
import com.gemastik.hemora.presentation.auth.login.LoginViewModel
import com.gemastik.hemora.presentation.auth.register.RegisterScreen
import com.gemastik.hemora.presentation.auth.register.RegisterViewModel
import com.gemastik.hemora.ui.theme.HemoraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HemoraTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            val viewModel: LoginViewModel = viewModel(factory = ViewModelFactory.Factory)
                            LoginScreen(
                                viewModel = viewModel,
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                },
                                onLoginSuccess = {
                                    // Navigate to home later
                                }
                            )
                        }
                        composable("register") {
                            val viewModel: RegisterViewModel = viewModel(factory = ViewModelFactory.Factory)
                            RegisterScreen(
                                viewModel = viewModel,
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onRegisterSuccess = {
                                    navController.navigate("login") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}