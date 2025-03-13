package com.example.kaloritakip.ui.screen

import android.widget.Space
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kaloritakip.data.model.ExerciseLog
import com.example.kaloritakip.data.model.FoodItem
import com.example.kaloritakip.data.model.UserInfo
import com.example.kaloritakip.ui.viewmodel.ExerciseLogUiState
import com.example.kaloritakip.ui.viewmodel.ExerciseViewModel
import com.example.kaloritakip.ui.viewmodel.UserInfoState
import com.example.kaloritakip.ui.viewmodel.UserViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.math.min

@Composable
fun HomeScreen(
    viewModel: UserViewModel,
    exerciseViewModel: ExerciseViewModel,
    onAddMeal: (String) -> Unit
) {
    val userInfo by viewModel.userInfoState.collectAsState()
    val bmr by viewModel.bmrState.collectAsState()
    val meals by viewModel.meals.collectAsState()
    val exerciseLogState by exerciseViewModel.logUiState.collectAsState()

    val totalCalories by viewModel.totalCalories.collectAsState()
    val totalCarbs by viewModel.totalCarbs.collectAsState()
    val totalProtein by viewModel.totalProtein.collectAsState()
    val totalFat by viewModel.totalFat.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Egzersizler", "Öğünler")

    LaunchedEffect(Unit) {
        viewModel.getUserInfo()
        viewModel.getMeals()
        viewModel.calculateTotalNutrients()
        exerciseViewModel.getTodayExercises()
    }

    LaunchedEffect(userInfo) {
        userInfo?.let { viewModel.calculateBMR(it) }
    }

    LaunchedEffect(meals) {
        viewModel.calculateTotalNutrients()
    }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFEEEEEE), Color(0xFFD6D6D6))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Hoş Geldin ${userInfo?.name ?: "👋"}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2B2D42)
                )
                Spacer(Modifier.height(16.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFFFFFFF),
                    shadowElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Günlük Hedef", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Text(text = "${bmr.toInt()} kcal", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    NutrientCard("🔥 Kalori", "${totalCalories.toInt()} kcal", Color(0xFFFF5722))
                    NutrientCard("🍞 Karbonhidrat", "${totalCarbs.toInt()} g", Color(0xFFFFC107))
                    NutrientCard("💪 Protein", "${totalProtein.toInt()} g", Color(0xFF4CAF50))
                    NutrientCard("🥑 Yağ", "${totalFat.toInt()} g", Color(0xFF795548))
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Toplam Kalori: ${totalCalories.toInt()} / ${bmr.toInt()} kcal",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = min(totalCalories.toFloat() / (bmr.toFloat().coerceAtLeast(1f)), 1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    color = Color(0xFF6A11CB),
                    trackColor = Color.LightGray
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    tabs.forEachIndexed { index, tab ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = index }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = tab,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) MaterialTheme.colorScheme.primary else Color.Gray
                            )
                            if (selectedTab == index) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Box(
                                    modifier = Modifier
                                        .width(40.dp)
                                        .height(3.dp)
                                        .background(
                                            MaterialTheme.colorScheme.primary,
                                            RoundedCornerShape(1.5.dp)
                                        )
                                )
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (selectedTab) {
                    0 -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                Text(
                                    "Bugünkü Egzersizler",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF333333),
                                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                )
                            }

                            when (exerciseLogState) {
                                is ExerciseLogUiState.Loading -> {
                                    item {
                                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator()
                                        }
                                    }
                                }
                                is ExerciseLogUiState.Success -> {
                                    val exercises = (exerciseLogState as ExerciseLogUiState.Success).exerciseLogs
                                    if (exercises.isNotEmpty()) {
                                        items(exercises) { exercise ->
                                            ExerciseLogItem(exercise, onDelete = { exerciseViewModel.deleteExercise(exercise.id) })
                                        }
                                    } else {
                                        item {
                                            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                                Text("Bugün eklenen egzersiz yok", fontSize = 16.sp, color = Color.Gray)
                                            }
                                        }
                                    }
                                }
                                is ExerciseLogUiState.Error -> {
                                    item { Text("Egzersizler yüklenemedi", color = Color.Red) }
                                }
                                else -> {}
                            }

                            item {
                                Button(
                                    onClick = { },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("+ Yeni Egzersiz Ekle", color = MaterialTheme.colorScheme.onPrimaryContainer)
                                }
                            }
                        }
                    }
                    1 -> {
                        val mealTypes = listOf("Kahvaltı", "Ara Öğün", "Öğle Yemeği", "Akşam Yemeği")

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 80.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                Text(
                                    "Öğünler",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF333333),
                                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                )
                            }

                            items(mealTypes) { mealType ->
                                MealCard(
                                    mealName = mealType,
                                    meals = meals.filter { it.mealType == mealType },
                                    onAddMeal = { onAddMeal(mealType) },
                                    onRemoveMeal = { foodItem ->
                                        viewModel.removeMeal(foodItem)
                                    }
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
fun NutrientCard(label: String, value: String, color: Color) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun MealCard(
    mealName: String,
    meals: List<FoodItem>,
    onAddMeal: (String) -> Unit,
    onRemoveMeal: (FoodItem) -> Unit
) {
    val mealIcons = mapOf(
        "Kahvaltı" to "🍳",
        "Öğle Yemeği" to "🍲",
        "Akşam Yemeği" to "🍽️",
        "Ara Öğün" to "🥪"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { },
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAFAFA))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = mealIcons[mealName] ?: "🍴",
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = mealName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Button(
                    onClick = { onAddMeal(mealName) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "+ Ekle",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 12.sp
                    )
                }
            }

            val totalMealCalories = meals.sumOf { it.nf_calories.toInt() }
            if (meals.isNotEmpty()) {
                Text(
                    text = "Toplam: $totalMealCalories kcal",
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (meals.isEmpty()) {
                Text(
                    text = "Bu öğüne henüz yemek eklenmedi.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 150.dp)
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(meals) { meal ->
                        MealItemCard(meal, onRemoveMeal)
                    }
                }
            }
        }
    }
}

@Composable
fun MealItemCard(meal: FoodItem, onRemoveMeal: (FoodItem) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meal.food_name,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = Color(0xFF444444)
                )
                Text(
                    text = "${meal.nf_calories.toInt()} kcal",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            IconButton(
                onClick = { onRemoveMeal(meal) },
                modifier = Modifier
                    .size(28.dp)
                    .background(Color(0xFFFFEBEE), RoundedCornerShape(4.dp))
            ) {
                Text("×", color = Color(0xFFE57373), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ExerciseLogItem(exerciseLog: ExerciseLog, onDelete: (ExerciseLog) -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(40.dp)
                    .background(Color(0xFF9C27B0), RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exerciseLog.exerciseName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Set: ${exerciseLog.sets}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(Color.LightGray, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tekrar: ${exerciseLog.reps}",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            IconButton(
                onClick = { onDelete(exerciseLog) },
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Sil",
                    tint = Color(0xFFE57373),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}