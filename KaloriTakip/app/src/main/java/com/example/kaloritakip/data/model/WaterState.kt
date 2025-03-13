package com.example.kaloritakip.data.model

data class WaterState(
    val totalDrunk : Int = 0,
    val pastRecords: List<WaterRecord> = emptyList()
)

data class WaterRecord(
    val date :String,
    val amount : Int
)