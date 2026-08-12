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
                            com.gemastik.hemora.presentation.home.HomeRemajaPutriScreen(
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                onNavigateToEducationDetail = { educationId ->
                                    navController.navigate("education_detail/$educationId")
                                }
                            )
                        }
                        composable(
                            route = "education_detail/{educationId}",
                            arguments = listOf(navArgument("educationId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val educationId = backStackEntry.arguments?.getString("educationId") ?: ""
                            val viewModel: com.gemastik.hemora.presentation.education.EducationViewModel = viewModel(factory = ViewModelFactory.Factory)
                            com.gemastik.hemora.presentation.education.EducationDetailScreen(
                                educationId = educationId,
                                viewModel = viewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("dashboard_uks") {
                            val viewModel: DashboardUksViewModel = viewModel(factory = ViewModelFactory.Factory)
                            DashboardUksScreen(
                                viewModel = viewModel,
                                onNavigateToSchedule = {
                                    navController.navigate("schedules")
                                },
                                onNavigateToSchoolCode = {
                                    navController.navigate("school_code")
                                },
                                onNavigateToStudents = {
                                    navController.navigate("daftar_siswi")
                                },
                                onNavigateToSchoolStatistics = {
                                    navController.navigate("school_statistics")
                                },
                                onNavigateToLogin = {
                                    navController.navigate("login") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("school_statistics") {
                            val viewModel: com.gemastik.hemora.presentation.dashboard_uks.SchoolStatisticsViewModel = viewModel(factory = ViewModelFactory.Factory)
                            com.gemastik.hemora.presentation.dashboard_uks.SchoolStatisticsScreen(
                                viewModel = viewModel,
                                onNavigateBack = {
                                    navController.navigateUp()
                                }
                            )
                        }
                        composable("daftar_siswi") {
                            val viewModel: com.gemastik.hemora.presentation.monitoring.DaftarSiswiViewModel = viewModel(factory = ViewModelFactory.Factory)
                            com.gemastik.hemora.presentation.monitoring.DaftarSiswiScreen(
                                viewModel = viewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                },
                                onStudentClick = { student ->
                                    navController.navigate("monitoring_detail/${student.userId}/${student.name}")
                                }
                            )
                        }
                        composable(
                            route = "monitoring_detail/{studentId}/{studentName}",
                            arguments = listOf(
                                navArgument("studentId") { type = NavType.StringType },
                                navArgument("studentName") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
                            val studentName = backStackEntry.arguments?.getString("studentName") ?: ""
                            val viewModel: com.gemastik.hemora.presentation.monitoring.DetailMonitoringViewModel = viewModel(factory = ViewModelFactory.Factory)
                            com.gemastik.hemora.presentation.monitoring.DetailMonitoringScreen(
                                navController = navController,
                                studentId = studentId,
                                studentName = studentName,
                                viewModel = viewModel
                            )
                        }
                        composable("school_code") {
                            val viewModel: com.gemastik.hemora.presentation.school.SchoolViewModel = viewModel(factory = ViewModelFactory.Factory)
                            com.gemastik.hemora.presentation.school.SchoolScreen(
                                viewModel = viewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
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