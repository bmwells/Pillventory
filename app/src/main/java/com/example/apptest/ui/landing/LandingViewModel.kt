package com.example.apptest.ui.landing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptest.data.AuthRepository
import com.example.apptest.data.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Background View model for landing screen
@HiltViewModel
class LandingViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    // Records whether user has signed in as guest
    val _guestState = Channel<GuestState>()
    val guestState = _guestState.receiveAsFlow()

    // Guest user enables anonymous user and user can use application without giving their personal information
    fun guestUse() = viewModelScope.launch {
        repository.anonymousUser().collect{result ->
            when(result) {
                is Resource.Success -> {
                    _guestState.send(GuestState(isSuccess = "Guest Access"))
                }
                is Resource.Loading -> {
                    _guestState.send(GuestState(isLoading = true))
                }
                is Resource.Error -> {
                    _guestState.send(GuestState(isError = result.message))
                }
            }
        }
    }
}

data class GuestState(
    val isLoading: Boolean = false,
    val isSuccess: String? = "",
    val isError: String? = ""
)