package com.example.apptest.ui.settings.profilesettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptest.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

    @HiltViewModel
    class ProfileSettingsViewModel @Inject constructor(
        private val repository: AuthRepository
    ) : ViewModel() {

        val logoutState = Channel<LogoutState>()

        fun logoutUser() {
            viewModelScope.launch {
                try {
                    repository.logoutUser()
                    logoutState.send(LogoutState(isSuccess = "Logout Success"))
                } catch (e: Exception) {
                    logoutState.send(LogoutState(isError = e.message ?: "Logout error"))
                }
            }
        }


        // Testing if you can retrieve the user's email :D
        private val _email = MutableStateFlow("")
        val email = _email

        private fun promptForEmail() = viewModelScope.launch { // Updates the value stored in email
            email.value = repository.getUserEmail().toString()
        }

        fun getUserEmail(): String { // <-- call this one in the composable
            promptForEmail()
            return email.value
        }

        fun sendVerification(emailAddress: String) = viewModelScope.launch {
            repository.sendVerification(emailAddress)
        }

    }

data class LogoutState(
    val isLoading: Boolean = false,
    val isSuccess: String? = null,
    val isError: String? = null
)

