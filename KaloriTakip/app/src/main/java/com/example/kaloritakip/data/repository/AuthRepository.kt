package com.example.kaloritakip.data.repository

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(private val auth:FirebaseAuth) {

    suspend fun login(email:String, password:String) : Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email,password).await()
            Result.success(Unit)
        } catch(e:Exception){
            Result.failure(e)
        }
    }

    suspend fun register(email:String, password:String) : Result<Unit> {
        return try {
            auth.createUserWithEmailAndPassword(email,password).await()
            Result.success(Unit)
        } catch(e:Exception){
            Result.failure(e)
        }
    }

    suspend fun logOut() : Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e:Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUser() = auth.currentUser
}