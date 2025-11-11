package com.example.apptest.ui.landing.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptest.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    val _forgotPasswordState = Channel<ForgotPasswordState>()
    val forgotPasswordState = _forgotPasswordState.receiveAsFlow()

    private val _userEmail = MutableStateFlow("")
    val userEmailforPass = _userEmail

    fun onEmailChange(editEmail: String) {
        _userEmail.value = editEmail
    }

    fun sendVerification(emailAddress: String) = viewModelScope.launch {
        repository.sendVerification(emailAddress)
        }
    }

data class ForgotPasswordState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = ""
)