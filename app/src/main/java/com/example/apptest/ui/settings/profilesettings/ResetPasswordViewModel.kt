package com.example.apptest.ui.settings.profilesettings

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
class ResetPasswordViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _userEmail = MutableStateFlow("")
    val userEmailforPass = _userEmail

    fun onEmailChange(editEmail: String) {
        _userEmail.value = editEmail
    }

    fun sendVerification(emailAddress: String) = viewModelScope.launch {
        repository.sendVerification(emailAddress)
    }
}
