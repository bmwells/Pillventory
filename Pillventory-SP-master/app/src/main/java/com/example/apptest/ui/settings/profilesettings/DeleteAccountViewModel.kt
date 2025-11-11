package com.example.apptest.ui.settings.profilesettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptest.data.AuthRepository
import com.example.apptest.data.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

    @HiltViewModel
    class DeleteAccountViewModel @Inject constructor(
        private val repository: AuthRepository,
        private val firebaseAuth: FirebaseAuth
    ) : ViewModel() {

        val deleteState = Channel<DeleteState>()

        fun deleteUser() {
            viewModelScope.launch {
                try {
                    repository.deleteUser()
                    deleteState.send(DeleteState(isSuccess = "Account Deletion Success"))
                } catch (e: Exception) {
                    deleteState.send(DeleteState(isError = e.message ?: "Unknown error occurred"))
                }
            }
        }

        // Login the user with provided email and password
        fun loginUser(email: String, password: String): Flow<Resource<AuthResult>> {
            return flow {
                emit(Resource.Loading())
                val result = firebaseAuth.signInWithEmailAndPassword(email, password).await() // wait until firebase sign in with email and password op is done and get result
                emit(Resource.Success(result))
            }.catch {
                emit(Resource.Error(it.message.toString())) // represent system error messages
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


    }

data class DeleteState(
    val isLoading: Boolean = false,
    val isSuccess: String? = null,
    val isError: String? = null
)

