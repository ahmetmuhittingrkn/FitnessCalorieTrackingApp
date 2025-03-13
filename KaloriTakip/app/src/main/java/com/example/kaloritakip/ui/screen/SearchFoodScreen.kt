package com.example.kaloritakip.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kaloritakip.data.model.FoodItem
import com.example.kaloritakip.data.model.TurkishFoodItem
import com.example.kaloritakip.ui.viewmodel.FoodUiState
import com.example.kaloritakip.ui.viewmodel.FoodViewModel

@Composable
fun SearchFoodScreen(viewModel: FoodViewModel, onFoodAdded: (TurkishFoodItem, Double) -> Unit) {
    var query by remember { mutableStateOf("") }
    val foodState by viewModel.foodState.collectAsState()
    var selectedFood by remember { mutableStateOf<TurkishFoodItem?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("🍏 Besin Ara...") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { viewModel.searchFood(query) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A))
        ) {
            Text("🔍 Ara", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (foodState) {
            is FoodUiState.Idle -> {
                Text("Lütfen bir arama yapın.", color = Color.Gray, fontSize = 16.sp)
            }
            is FoodUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is FoodUiState.Success -> {
                val foods = (foodState as FoodUiState.Success).foods
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(foods) { foodItem ->
                        FoodItemCard(foodItem, onAddClick = { selectedFood = it })
                    }
                }
            }
            is FoodUiState.Error -> {
                val errorMessage = (foodState as FoodUiState.Error).message
                Text("Hata: $errorMessage", color = Color.Red)
            }
        }
    }

    selectedFood?.let { food ->
        PortionSelectionDialog(food = food, onDismiss = { selectedFood = null }, onConfirm = { quantity, unitType ->
            val totalCalories = when (unitType) {
                "Gram" -> (food.calories / food.servingWeightGrams) * quantity
                "Adet" -> food.calories * quantity
                else -> 0.0
            }
            onFoodAdded(food, totalCalories)
            selectedFood = null
        })
    }
}

@Composable
fun FoodItemCard(food: TurkishFoodItem, onAddClick: (TurkishFoodItem) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF81C784), Color(0xFFA5D6A7))
                )
            )
            .padding(12.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = food.foodName, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                NutrientInfo("🔥 Kalori", "${food.calories} kcal")
                NutrientInfo("💪 Protein", "${food.protein} g")
                NutrientInfo("🥔 Karbonhidrat", "${food.carbohydrate} g")
                NutrientInfo("🥑 Yağ", "${food.fat} g")
            }

            Text(
                text = "🍽️ Porsiyon: ${food.servingQty} ${food.servingUnit} (${food.servingWeightGrams}g)",
                style = MaterialTheme.typography.bodySmall,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onAddClick(food) },
                modifier = Modifier.align(Alignment.End),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
            ) {
                Text("➕ Ekle", color = Color.White)
            }
        }
    }
}

@Composable
fun PortionSelectionDialog(food: TurkishFoodItem, onDismiss: () -> Unit, onConfirm: (Int, String) -> Unit) {
    var quantity by remember { mutableStateOf("") }
    var unitType by remember { mutableStateOf("Gram") }
    val unitOptions = listOf("Gram", "Adet")

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                onClick = { onConfirm(quantity.toIntOrNull() ?: 100, unitType) },
                enabled = quantity.isNotEmpty() && quantity.toIntOrNull() != null,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
            ) {
                Text("Ekle")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))) {
                Text("İptal")
            }
        },
        title = { Text(text = "Porsiyon Seçimi") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("${food.foodName} için miktar giriniz:")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    unitOptions.forEach { option ->
                        Button(
                            onClick = { unitType = option },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (unitType == option) Color(0xFF8BC34A) else Color.LightGray
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(option, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.matches(Regex("^[0-9]*\$"))) {
                            quantity = newValue
                        }
                    },
                    label = { Text(unitType) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Composable
fun NutrientInfo(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(text = value, color = Color.DarkGray, fontSize = 13.sp)
    }
}
