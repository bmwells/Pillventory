package com.example.apptest.ui.landing.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptest.data.AuthRepository
import com.example.apptest.data.Resource
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// Background View model for login
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    /**
     * Track variables
     */
    val _loginState = Channel<LoginState>() // Defined below viewmodel -> Success, loading, error
    val loginState = _loginState.receiveAsFlow()

    private val _userEmail = MutableStateFlow("") // Stores the entered user email
    val userEmail = _userEmail

    private val _userPass = MutableStateFlow("") // Stores the entered user password
    val userPass = _userPass

    private val _userID = MutableStateFlow("")
    val userID = _userID

    // Function to prompt system to retrieve UID
    private fun promptUserUID() = viewModelScope.launch {
        _userID.value = repository.getUID().toString()
    }

    // Function to be used in viewmodel for saving items with UID
    private fun getUserID() : String {
        promptUserUID()
        return userID.value
    }

    /**
     * Functions for login viewmodel
     */
    fun onEmailChange(editEmail: String) { // Updates the value of email when user types
        _userEmail.value = editEmail
    }

    fun onPassChange(editPass: String) { // Updates the value of password when user types
        _userPass.value = editPass
    }

    fun loginUser(email: String, password: String) = viewModelScope.launch {
        repository.loginUser(email, password).collect { result ->
            when (result) {
                is Resource.Success -> {
                    // Check if the user is verified before allowing login
                    if (repository.isCurrentUserVerified()) {
                        _loginState.send(LoginState(isSuccess = "Login Success")) // If successful this message will appear
                    } else {
                        _loginState.send(LoginState(isError = "Please verify your email to Login"))
                    }
                }
                is Resource.Loading -> {
                    _loginState.send(LoginState(isLoading = true))
                }
                is Resource.Error -> {
                    _loginState.send(LoginState(isError = result.message)) // System error message
                }
            }
        }
    }

    // Firebase against using "." in pathways so they are removed when stored for this section
    private fun removePeriods(email: String): String {
        // replaced . with "" blank in string
        return email.replace(".", "")
    }

    // want to store the user's email and id to be referenced later
    suspend fun storeInfo(email: String) {
        val database = Firebase.database.reference
        val tempEmailStore = removePeriods(email) // cannot have path include ".", "[", "]" etc.

        val userId = getUserID()

        val user = UserInfo(
            userEmail = tempEmailStore,
            userId = userId
        )


        val infoPathReference = database.child("user info").child(tempEmailStore)
        val snapshot = infoPathReference.get().await()

        if(!snapshot.exists()) { // prevent overwriting information already stored in database
            infoPathReference.setValue(user).addOnSuccessListener {
                println("User {$email} saved")
            }
            .addOnFailureListener{
                //
            }
        } else {
            println("User info already recorded")
        }
    }
}


// data classes:
data class LoginState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = ""
)

data class UserInfo(
    val userEmail: String,
    val userId: String
)