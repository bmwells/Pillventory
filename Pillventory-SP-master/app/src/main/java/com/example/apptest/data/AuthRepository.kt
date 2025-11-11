package com.example.apptest.data

import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun loginUser(email: String, password: String): Flow<Resource<AuthResult>>
    fun registerUser(email: String, password: String): Flow<Resource<AuthResult>>
    fun anonymousUser(): Flow<Resource<AuthResult>>
    fun deleteUser()
    fun logoutUser(): Flow<Resource<Boolean>>

    fun getUID(): String?
    fun getUserEmail(): String?

    fun isUserAnonymous(): Boolean

    fun sendVerification(emailAddress: String)

    fun sendUserEmailVerification()

    fun isCurrentUserVerified(): Boolean

    fun setItemRecordId(itemId: String)
    fun getItemRecordId(): String?



}