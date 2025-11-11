package com.example.apptest.ui.settings.profilesettings

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.apptest.PillTopAppBar
import com.example.apptest.R
import com.example.apptest.data.Resource
import com.example.apptest.ui.navigation.NavigationDestination
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow

object DeleteAccountDestination: NavigationDestination {
    override val route = "delete account"
    override val titleRes = R.string.delete_acc_title // Added to XML because this is type Int
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAccountScreen(
    navController: NavHostController,
    navigateToLandingPage: () -> Unit
    ) {
    Scaffold(
        topBar = {
            PillTopAppBar(
                title = stringResource(id = DeleteAccountDestination.titleRes),
                navigateUp = { navController.popBackStack() },
                canNavigateBack = true,
            )
        }
    ) { innerPadding ->
        DeleteAccountBody(
            modifier = Modifier.padding(innerPadding),
            navigateToLandingPage = navigateToLandingPage,
            navController = navController // Pass the navController instance
        )
    }
}

@Composable
fun DeleteAccountBody(
    modifier: Modifier,
    navController: NavHostController,
    navigateToLandingPage: () -> Unit,
    viewModel: DeleteAccountViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val userEmail = viewModel.getUserEmail()
    var isLoginSuccessful by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var reenterPassword by remember { mutableStateOf("") }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    var isPasswordVisible1 by remember { mutableStateOf(false) }
    var isPasswordVisible2 by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        // Text display
        Text(
            text = stringResource(id = R.string.confirm_deletion),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            textAlign = TextAlign.Center
        )

        // Display Email Address
        Text(
            text = viewModel.getUserEmail(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            textAlign = TextAlign.Center
        )

        // Text Field for email address
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(id = R.string.enter_email))},
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        // Text Field for password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(id = R.string.enter_pass))},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (isPasswordVisible1) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible1 = !isPasswordVisible1 }) {
                    Icon(painter =
                    if (isPasswordVisible1){
                        painterResource(id = R.drawable.outline_visibility_24)
                    } else {
                        painterResource(id = R.drawable.outline_visibility_off_24)
                    },
                        contentDescription = "Toggle password visibility" // can add to strings.xml
                    )
                }

            },
            modifier = Modifier.fillMaxWidth()
        )

        // Text Field for Reenter password
        OutlinedTextField(
            value = reenterPassword,
            onValueChange = { reenterPassword = it },
            label = { Text(stringResource(id = R.string.reenter_pass))},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (isPasswordVisible2) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible2 = !isPasswordVisible2 }) {
                    Icon(painter =
                    if (isPasswordVisible2){
                        painterResource(id = R.drawable.outline_visibility_24)
                    } else {
                        painterResource(id = R.drawable.outline_visibility_off_24)
                    },
                        contentDescription = "Toggle password visibility" // can add to strings.xml
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Delete Button
        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty() && reenterPassword.isNotEmpty()
                    && password == reenterPassword && email == userEmail) {
                    showDeleteConfirmation = true

                    // Perform login before delete and observe the result
                    viewModel.loginUser(email, password).onEach { loginResult ->
                        if (loginResult is Resource.Success && loginResult.data != null) {
                            // Login successful
                            isLoginSuccessful = true
                            viewModel.deleteUser()
                        }
                        else{
                            Toast.makeText(context, R.string.credential_error, Toast.LENGTH_SHORT).show()
                        }
                    }.launchIn(viewModel.viewModelScope) // Launch the flow in the ViewModel's scope
                }
            },
            enabled = email.isNotEmpty() && password.isNotEmpty() && reenterPassword.isNotEmpty()
                    && password == reenterPassword && email == userEmail,
            colors = ButtonDefaults.buttonColors(Color.Red),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(text = stringResource(id = R.string.delete_acc_title)) // Add to strings xml
        }

        // Cancel Button
        Button(
            onClick = { navController.popBackStack() }, // Go back to the previous page
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(text = stringResource(id = R.string.cancel)) // Add to strings xml
        }

        // Observe delete state and show toast messages based on login success
        LaunchedEffect(viewModel.deleteState) {
            viewModel.deleteState.receiveAsFlow().collect { state ->
                if (state.isSuccess != null && isLoginSuccessful) {
                    // Only show toast message if delete was successful
                    Toast.makeText(context, R.string.acc_delete_confirm, Toast.LENGTH_LONG).show()
                    navigateToLandingPage()
                } else {
                    // Show error message if delete failed
                    Toast.makeText(context, R.string.credential_error, Toast.LENGTH_SHORT).show()
                }
            }
        }



    }
}
