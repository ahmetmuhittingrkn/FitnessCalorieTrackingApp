package com.example.kaloritakip.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Ana Sayfa")
    object Fitness : BottomNavItem("fitness", Icons.Default.FitnessCenter, "Fitness")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Profil")
    object Water : BottomNavItem("water", Icons.Default.LocalDrink, "Su Takibi")
}