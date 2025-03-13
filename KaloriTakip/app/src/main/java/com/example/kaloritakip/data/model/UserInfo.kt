package com.example.kaloritakip.data.model

data class UserInfo(val name:String = "",
                    val age:Int=0,
                    val gender:String="",
                    val email : String = "",
                    val goal : String = "",
                    val bmr : Double = 0.0,
                    val height:Int=0,
                    val weight:Int=0,
                    val activityLevel: String = "",
                    val targetWeight:Int=0) {
}