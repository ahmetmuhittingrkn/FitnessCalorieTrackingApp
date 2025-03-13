package com.example.kaloritakip.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.SuggestionChip
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.textFieldColors
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.kaloritakip.data.model.UserInfo
import com.example.kaloritakip.ui.viewmodel.UserInfoState
import com.example.kaloritakip.ui.viewmodel.UserViewModel
import com.example.kaloritakip.util.BottomNavItem
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun UserInfoScreen(viewModel: UserViewModel, navController: NavHostController) {
    var step by remember { mutableStateOf(1) }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var activityLevel by remember { mutableStateOf("") }
    var targetWeight by remember { mutableStateOf("") }

    val statusState by viewModel.statusState.collectAsState()

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFF263238), Color(0xFF37474F))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Kişisel Bilgiler",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            StepIndicator(currentStep = step)

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                AnimatedContent(
                    targetState = step,
                    transitionSpec = {
                        slideInHorizontally { it } + fadeIn() with
                                slideOutHorizontally { -it } + fadeOut()
                    },
                    modifier = Modifier.padding(20.dp)
                ) { targetStep ->
                    when (targetStep) {
                        1 -> StepOne(name, age) { newName, newAge ->
                            name = newName
                            age = newAge
                            step++
                        }
                        2 -> StepTwo(gender) { selectedGender ->
                            gender = selectedGender
                            step++
                        }
                        3 -> StepThree(height, weight, targetWeight) { newHeight, newWeight, newTargetWeight ->
                            height = newHeight
                            weight = newWeight
                            targetWeight = newTargetWeight
                            step++
                        }
                        4 -> StepFive(activityLevel) { selectedActivity ->
                            activityLevel = selectedActivity
                            step++
                        }
                        5 -> StepFour(name, age, gender, height, weight, targetWeight,activityLevel) {
                            viewModel.saveUserInfo(
                                UserInfo(
                                    name = name,
                                    age = age.toIntOrNull() ?: 0,
                                    gender = gender,
                                    height = height.toIntOrNull() ?: 0,
                                    weight = weight.toIntOrNull() ?: 0,
                                    targetWeight = targetWeight.toIntOrNull() ?: 0,
                                    activityLevel = activityLevel,
                                )
                            )
                            navController.navigate(BottomNavItem.Home.route) {
                                popUpTo("user_info_screen") { inclusive = true }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (step > 1) {
                    OutlinedButton(
                        onClick = { step-- },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White,
                            containerColor = Color(0x33FFFFFF)
                        )
                    ) {
                        Text("Geri", fontWeight = FontWeight.Bold)
                    }
                }
            }
            when (statusState) {
                is UserInfoState.Loading -> CircularProgressIndicator(color = Color.White)
                is UserInfoState.Success -> {
                    LaunchedEffect(Unit) {
                        delay(1000)
                        viewModel.resetUserInfoState()
                    }
                    Text(
                        text = "Bilgiler başarıyla kaydedildi!",
                        color = Color.Green,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                is UserInfoState.Error -> Text(
                    (statusState as UserInfoState.Error).message,
                    color = Color.Red
                )
                else -> {}
            }
        }
    }
}

@Composable
fun StepIndicator(currentStep: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        repeat(5) { index ->
            val isActive = index + 1 == currentStep
            Box(
                modifier = Modifier
                    .size(if (isActive) 14.dp else 10.dp)
                    .clip(CircleShape)
                    .background(if (isActive) Color(0xFF4CAF50) else Color(0x88FFFFFF))
                    .padding(4.dp)
            )
            if (index != 4) Spacer(modifier = Modifier.width(8.dp))
        }
    }
}

@Composable
fun StepOne(name: String, age: String, onNext: (String, String) -> Unit) {
    var tempName by remember { mutableStateOf(name) }
    var tempAge by remember { mutableStateOf(age) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            value = tempName,
            onValueChange = { tempName = it },
            label = { Text("İsim") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF3F4F6),
                unfocusedContainerColor = Color(0xFFF3F4F6),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = tempAge,
            onValueChange = { tempAge= it },
            label = { Text("Yaş") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF3F4F6),
                unfocusedContainerColor = Color(0xFFF3F4F6),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { onNext(tempName, tempAge) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("İleri", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}


@Composable
fun StepTwo(gender: String, onNext: (String) -> Unit) {
    var selectedGender by remember { mutableStateOf(gender) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Cinsiyetinizi Seçin",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF4CAF50),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            GenderOption("Erkek", selectedGender) { selectedGender = it }
            GenderOption("Kadın", selectedGender) { selectedGender = it }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (selectedGender.isNotEmpty()) onNext(selectedGender)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedGender.isNotEmpty()) Color(0xFF4CAF50) else Color.Gray
            ),
            enabled = selectedGender.isNotEmpty()
        ) {
            Text("İleri", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun GenderOption(gender: String, selectedGender: String, onSelect: (String) -> Unit) {
    val isSelected = gender == selectedGender

    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(if (isSelected) Color(0xFF4CAF50) else Color(0xFFF3F4F6))
            .clickable { onSelect(gender) }
            .border(
                width = if (isSelected) 4.dp else 2.dp,
                color = if (isSelected) Color(0xFF388E3C) else Color.LightGray,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = gender,
            color = if (isSelected) Color.White else Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
fun StepThree(height: String, weight: String, targetWeight: String, onNext: (String, String, String) -> Unit) {
    var tempHeight by remember { mutableStateOf(height) }
    var tempWeight by remember { mutableStateOf(weight) }
    var tempTargetWeight by remember { mutableStateOf(targetWeight) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            value = tempHeight,
            onValueChange = { tempHeight = it },
            label = { Text("Boy (cm)") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF3F4F6),
                unfocusedContainerColor = Color(0xFFF3F4F6),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = tempWeight,
            onValueChange = { tempWeight = it },
            label = { Text("Kilo (kg)") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF3F4F6),
                unfocusedContainerColor = Color(0xFFF3F4F6),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = tempTargetWeight,
            onValueChange = { tempTargetWeight = it },
            label = { Text("Hedef Kilo (kg)") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF3F4F6),
                unfocusedContainerColor = Color(0xFFF3F4F6),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = Color.Black
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onNext(tempHeight, tempWeight, tempTargetWeight) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("İleri", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}


@Composable
fun StepFour(name: String, age: String, gender: String, height: String, weight: String, targetWeight: String,activityLevel: String, onSave: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Özet", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFF4CAF50))
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("İsim: $name", fontSize = 18.sp)
                Text("Yaş: $age", fontSize = 18.sp)
                Text("Cinsiyet: $gender", fontSize = 18.sp)
                Text("Boy: $height cm", fontSize = 18.sp)
                Text("Kilo: $weight kg", fontSize = 18.sp)
                Text("Hedef kilo: $targetWeight kg", fontSize = 18.sp)
                Text("Aktivite Seviyesi: $activityLevel", fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Bilgileri Kaydet", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}


@Composable
fun StepFive(activityLevel: String, onNext: (String) -> Unit) {
    var selectedActivity by remember { mutableStateOf(activityLevel) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Aktivite Seviyesi", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFF4CAF50))

        Spacer(modifier = Modifier.height(12.dp))

        ActivityOption("Düşük Aktivite", selectedActivity) { selectedActivity = it }
        ActivityOption("Orta Aktivite", selectedActivity) { selectedActivity = it }
        ActivityOption("Yüksek Aktivite", selectedActivity) { selectedActivity = it }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onNext(selectedActivity) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
            enabled = selectedActivity.isNotEmpty()
        ) {
            Text("İleri", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ActivityOption(activity: String, selectedActivity: String, onSelect: (String) -> Unit) {
    val isSelected = activity == selectedActivity

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) Color(0xFF4CAF50) else Color(0xFFF3F4F6))
            .clickable { onSelect(activity) }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = activity,
            color = if (isSelected) Color.White else Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

