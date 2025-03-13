package com.example.kaloritakip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kaloritakip.data.model.FoodItem
import com.example.kaloritakip.ui.components.BottomNavigationBar
import com.example.kaloritakip.ui.screen.*
import com.example.kaloritakip.ui.viewmodel.*
import com.example.kaloritakip.util.BottomNavItem
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigator()
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()
    val foodViewModel: FoodViewModel = hiltViewModel()
    val exerciseViewModel: ExerciseViewModel = hiltViewModel()
    val waterViewModel: WaterViewModel = hiltViewModel()

    val sessionState by authViewModel.sessionState.collectAsState()
    val userInfoState by userViewModel.userInfoState.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.checkUserSession()
    }

    LaunchedEffect(sessionState) {
        when (sessionState) {
            is SessionState.Authenticated -> {
                userViewModel.getUserInfo()
            }
            is SessionState.Unauthenticated -> {
                navController.navigate("login_screen") {
                    popUpTo(0) { inclusive = true }
                }
                userViewModel.resetUserInfoState()
            }
            else -> {}
        }
    }

    if (sessionState == SessionState.Idle) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val startDestination = when {
            sessionState is SessionState.Unauthenticated -> "login_screen"
            sessionState is SessionState.Authenticated && userInfoState == null -> "user_info_screen"
            sessionState is SessionState.Authenticated && userInfoState?.name.isNullOrEmpty() -> "user_info_screen"
            sessionState is SessionState.Authenticated -> BottomNavItem.Home.route
            else -> "login_screen"
        }

        Scaffold(
            bottomBar = {
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                if (currentRoute in listOf(
                        BottomNavItem.Home.route,
                        BottomNavItem.Fitness.route,
                        BottomNavItem.Profile.route,
                        BottomNavItem.Water.route
                    )
                ) {
                    BottomNavigationBar(navController = navController)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("login_screen") {
                    LoginScreen(navController, authViewModel, onLoginSuccess = {
                        navController.navigate(BottomNavItem.Home.route) {
                            popUpTo("login_screen") { inclusive = true }
                        }
                    })
                }
                composable("register_screen") {
                    RegisterScreen(navController, authViewModel)
                }
                composable("user_info_screen") {
                    UserInfoScreen(viewModel = userViewModel, navController = navController)
                }

                composable(BottomNavItem.Home.route) {
                    HomeScreen(
                        viewModel = userViewModel,
                        exerciseViewModel = exerciseViewModel,
                        onAddMeal = { mealType ->
                            navController.navigate("search_food_screen/$mealType")
                        }
                    )
                }
                composable(BottomNavItem.Fitness.route) {
                    FitnessScreen(viewModel = exerciseViewModel, navController = navController)
                }
                composable(BottomNavItem.Profile.route) {
                    ProfileScreen(
                        userViewModel = userViewModel,
                        authViewModel = authViewModel,
                        navController = navController
                    )
                }

                composable(BottomNavItem.Water.route) {
                    WaterTrackingScreen(viewModel = waterViewModel)
                }

                composable("exercise_detail_screen/{exerciseId}") { backStackEntry ->
                    val exerciseId = backStackEntry.arguments?.getString("exerciseId")
                    val uiState by exerciseViewModel.uiState.collectAsState()

                    if (uiState is ExerciseUiState.Success) {
                        val exercise = (uiState as ExerciseUiState.Success).exercises.find { it.id == exerciseId }
                        if (exercise != null) {
                            ExerciseDetailScreen(
                                exercise = exercise,
                                onAddExercise = { log ->
                                    exerciseViewModel.addExercise(log)
                                    navController.popBackStack()
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        } else {
                            Text("Egzersiz bulunamadı.")
                        }
                    } else {
                        CircularProgressIndicator()
                    }
                }

                composable("search_food_screen/{mealType}") { backStackEntry ->
                    val mealType = backStackEntry.arguments?.getString("mealType") ?: "Genel"
                    SearchFoodScreen(
                        viewModel = foodViewModel,
                        onFoodAdded = { turkishFood, calories ->
                            navController.popBackStack()

                            val mealWithId = FoodItem(
                                food_name = turkishFood.foodName,
                                nf_calories = calories,
                                nf_protein = turkishFood.protein,
                                nf_total_carbohydrate = turkishFood.carbohydrate,
                                nf_total_fat = turkishFood.fat,
                                serving_weight_grams = turkishFood.servingWeightGrams,
                                serving_qty = turkishFood.servingQty,
                                serving_unit = turkishFood.servingUnit,
                                mealType = mealType,
                                mealId = UUID.randomUUID().toString()
                            )
                            userViewModel.addMeal(mealWithId)
                        }
                    )
                }
                composable("home_screen") {
                    navController.navigate(BottomNavItem.Home.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            }
        }
    }
}