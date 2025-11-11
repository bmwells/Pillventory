package com.example.apptest.ui.landing.signup


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptest.data.AuthRepository
import com.example.apptest.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: AuthRepository // Access to repository functions
): ViewModel() {

    /**
     * Values stored for the main Sign Up page
     */

    // Stores the state of loading, success or error
    val _signUpState = Channel<SignUpState>()
    val signUpState = _signUpState.receiveAsFlow()

    // Stores the user email
    private val _userEmail = MutableStateFlow("")
    val userEmail = _userEmail // value accessed

    // Stores the first password entered
    private val _userPass1 = MutableStateFlow("")
    val userPass1 = _userPass1 // value accessed

    // Stores the second password entered
    private val _userPass2 = MutableStateFlow("")
    val userPass2 = _userPass2 // value accessed

    /**
     * Functions for sign up viewmodel
     */

    // When the email is changed, update the userEmail value
    fun onEmailChange(editEmail: String) {
        _userEmail.value = editEmail
    }

    // When the password is changed, update the userPass1 value
    fun onFirstPassChange(editPass: String) {
        _userPass1.value = editPass
    }

    // When the password is changed, update the userPass2 value
    fun onSecondPassChange(editPass: String) {
        _userPass2.value = editPass
    }

    // When signing in - check if the passwords match, return boolean
    private fun checkMatchPasswords(pass1: String, pass2: String) : Boolean {
        return pass1.isNotBlank() && pass2.isNotBlank() && pass1 == pass2
    }

    // Repository function call to create user using credentials entered in sign in screen
    fun createUser(email: String, password: String) = viewModelScope.launch {
        if(checkMatchPasswords(userPass1.value, userPass2.value)) { // Want to create a user when the passwords also match
            repository.registerUser(email, password).collect{ result ->
                when(result) {
                    is Resource.Success -> {
                        repository.sendUserEmailVerification();
                        _signUpState.send(SignUpState(isSuccess = "Sign Up Success"))
                    }
                    is Resource.Loading -> {
                        _signUpState.send(SignUpState(isLoading = true))
                    }
                    is Resource.Error -> {
                        _signUpState.send(SignUpState(isError = result.message)) // System error message
                    }
                }
            }
        } else {
            _signUpState.send(SignUpState(isError = "Passwords do not match")) // error message when passwords don't match and will not create account
        }
    }

    fun sendVerificationToUser() = viewModelScope.launch{
        repository.sendUserEmailVerification()
    }

}

// Define default state for sign up screen
data class SignUpState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = ""
)