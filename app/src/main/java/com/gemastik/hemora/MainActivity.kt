package com.gemastik.hemora

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.gemastik.hemora.presentation.auth.register_uks.RegisterUksScreen
import com.gemastik.hemora.presentation.auth.register_uks.RegisterUksViewModel
import com.gemastik.hemora.presentation.dashboard_uks.DashboardUksScreen
import com.gemastik.hemora.presentation.dashboard_uks.DashboardUksViewModel
import com.gemastik.hemora.presentation.schedule.AddEditScheduleScreen
import com.gemastik.hemora.presentation.schedule.ScheduleScreen
import com.gemastik.hemora.presentation.schedule.ScheduleViewModel
import com.gemastik.hemora.ui.theme.HemoraTheme
import androidx.navigation.NavType
import androidx.navigation.navArgument

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
                                onNavigateToRegisterRemajaPutri = {
                                    navController.navigate("register")
                                },
                                onNavigateToRegisterUks = {
                                    navController.navigate("register_uks")
                                },
                                onLoginSuccess = { role ->
                                    if (role == "REMAJA_PUTRI") {
                                        navController.navigate("home_remaja_putri") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } else if (role == "UKS") {
                                        navController.navigate("dashboard_uks") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
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
                        composable("register_uks") {
                            val viewModel: RegisterUksViewModel = viewModel(factory = ViewModelFactory.Factory)
                            RegisterUksScreen(
                                viewModel = viewModel,
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onRegisterSuccess = {
                                    navController.navigate("login") {
                                        popUpTo("register_uks") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("home_remaja_putri") {
                            Text("Welcome to Home Remaja Putri")
                        }
                        composable("dashboard_uks") {
                            val viewModel: DashboardUksViewModel = viewModel(factory = ViewModelFactory.Factory)
                            DashboardUksScreen(
                                viewModel = viewModel,
                                onNavigateToSchedule = {
                                    navController.navigate("schedules")
                                }
                            )
                        }
                        composable("schedules") {
                            val viewModel: ScheduleViewModel = viewModel(factory = ViewModelFactory.Factory)
                            ScheduleScreen(
                                viewModel = viewModel,
                                onNavigateToAddEdit = { scheduleId, date, time ->
                                    if (scheduleId == null) {
                                        navController.navigate("add_edit_schedule")
                                    } else {
                                        navController.navigate("add_edit_schedule?scheduleId=$scheduleId&date=$date&time=$time")
                                    }
                                }
                            )
                        }
                        composable(
                            route = "add_edit_schedule?scheduleId={scheduleId}&date={date}&time={time}",
                            arguments = listOf(
                                navArgument("scheduleId") { type = NavType.StringType; nullable = true; defaultValue = null },
                                navArgument("date") { type = NavType.StringType; nullable = true; defaultValue = null },
                                navArgument("time") { type = NavType.StringType; nullable = true; defaultValue = null }
                            )
                        ) { backStackEntry ->
                            val viewModel: ScheduleViewModel = viewModel(factory = ViewModelFactory.Factory)
                            val scheduleId = backStackEntry.arguments?.getString("scheduleId")
                            val date = backStackEntry.arguments?.getString("date")
                            val time = backStackEntry.arguments?.getString("time")
                            
                            AddEditScheduleScreen(
                                viewModel = viewModel,
                                scheduleId = scheduleId,
                                initialDate = date,
                                initialTime = time,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}