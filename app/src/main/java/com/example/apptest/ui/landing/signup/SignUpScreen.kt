package com.example.apptest.ui.landing.signup


import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.apptest.PillTopAppBar
import com.example.apptest.R
import com.example.apptest.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

// Defines related information to the Sign Up page - route and title reference
object SignUpDestination : NavigationDestination {
    override val route = "sign up"
    override val titleRes = R.string.signup_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navigateVerify: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    onEnterClick : () -> Unit // May change with authentication (not sure if Firebase allow 2FA on free Spark acc)
) {
    Scaffold ( // Used to define the top bar
        topBar = {
            PillTopAppBar(
                title = stringResource(SignUpDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        SignUpBody( // Calls composable for Login elements for user to interact with
            navigateVerify = navigateVerify,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}

@Composable
fun SignUpBody(
    navigateVerify: () -> Unit,
    viewModel : SignUpViewModel = hiltViewModel(), // ViewModel is access to backend storing variables or related functionality
    modifier: Modifier // Ensure body stays below the topbar
) {

    /**
     *  Variables:
     */
    var isPasswordVisible1 by remember { mutableStateOf(false) }
    var isPasswordVisible2 by remember { mutableStateOf(false) }

    val userEmail = viewModel.userEmail.collectAsState()
    val userPassword = viewModel.userPass2.collectAsState()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val state = viewModel.signUpState.collectAsState(initial = null)

    /**
     * Composable elements:
     */
    Column( // Sign Up header message
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Sign Up!",
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Center
        )
    }

    Column (
        modifier = modifier.padding(16.dp)
    ) {

        Spacer(modifier = Modifier.height(30.dp)) // Creates small gaps between composable elements

        // Text box for Enter email address
        OutlinedTextField(
            value = viewModel.userEmail.collectAsState().value,
            onValueChange = { viewModel.onEmailChange(it) }, // Update value seen as user types it
            label = { Text(text = "Enter email address")}, // Add text to strings.xml
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true, // Keeps all text on one line/prevents user from creating a new line
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Text box for Enter first password
        // Extra would be to check for special characters in the viewModel, but later
        OutlinedTextField(
            value = viewModel.userPass1.collectAsState().value,
            onValueChange = { viewModel.onFirstPassChange(it)}, // Update value seen as user types it
            label = { Text(text = "Enter password")}, // Add text to strings.xml,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (isPasswordVisible1) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true, // Keeps all text on one line/prevents user from creating a new line
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

        Spacer(modifier = Modifier.height(16.dp))

        // Text box for Enter second password or confirm password :)
        OutlinedTextField(
            value = viewModel.userPass2.collectAsState().value,
            onValueChange = { viewModel.onSecondPassChange(it)}, // Update value seen as user types it
            label = { Text(text = "Confirm password")}, // Add text to strings.xml,
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

        Spacer(modifier = Modifier.height(35.dp))

        // Button for Sign Up / submit info
        Button(
            onClick = {
                scope.launch {
                    viewModel.createUser(userEmail.value, userPassword.value);
                    viewModel.sendVerificationToUser()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Sign Up")
        }


        // Bottom notifications for success messages (Toast)
        LaunchedEffect(key1 = state.value?.isSuccess) {
            scope.launch {
                if(state.value?.isSuccess?.isNotEmpty() == true) {
                    val success = state.value?.isSuccess
                    Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                    navigateVerify()

                }
            }
        }

        // Bottom notifications for error messages (Toast)
        LaunchedEffect(key1 = state.value?.isError) {
            scope.launch {
                if(state.value?.isError?.isNotEmpty() == true) {
                    val error = state.value?.isError
                    Toast.makeText(context, "$error", Toast.LENGTH_LONG).show()

                }
            }
        }
    }
}

// Preview Screen without running emulator
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpScreenPreview() {
    SignUpScreen(
        onNavigateUp = {},
        canNavigateBack = true,
        onEnterClick = {},
        navigateVerify = {}
    )
}