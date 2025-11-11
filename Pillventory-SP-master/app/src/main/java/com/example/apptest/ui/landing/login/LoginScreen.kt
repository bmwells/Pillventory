package com.example.apptest.ui.landing.login


import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.apptest.PillTopAppBar
import com.example.apptest.R
import com.example.apptest.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

// Defines related information to the Login page - route and title reference
object LoginDestination : NavigationDestination {
    override val route = "login"
    override val titleRes = R.string.login_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    navigateForgotPass: () -> Unit,
    navigateCamera : () -> Unit
) {
    // Not going to specify scroll behavior because we don't want it to scroll

    Scaffold ( // Used to define the top bar
        topBar = {
            PillTopAppBar(
                title = stringResource(LoginDestination.titleRes), // Display the title reference in the top bar
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        LoginBody( // Calls composable for Login elements for user to interact with
            navigateForgotPass = navigateForgotPass,
            navigateCamera = navigateCamera,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }

}

@Composable
private fun LoginBody(
    navigateForgotPass: () -> Unit,
    navigateCamera : () -> Unit,
    viewModel : LoginViewModel = hiltViewModel(), // ViewModel is access to backend storing variables or related functionality
    modifier : Modifier, // Ensure body stays below the topbar
) {
    /**
     *  Variables:
     */
    var isPasswordVisible by remember {mutableStateOf(false)}

    val userEmail = viewModel.userEmail.collectAsState()
    val userPassword = viewModel.userPass.collectAsState()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val state = viewModel.loginState.collectAsState(initial = null)

    /**
     * Composable elements:
     */
    Column( // Welcome greeting for returning users
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Hi, Welcome!",
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
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Text box for Enter password
        OutlinedTextField(
            value = viewModel.userPass.collectAsState().value,
            onValueChange = { viewModel.onPassChange(it)}, // Update value seen as user types it
            label = { Text(text = "Enter password")}, // Add text to strings.xml,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(), // Masks password
            singleLine = true, // Keeps all text on one line/prevents user from creating a new line
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) { // Determines password visibility
                    Icon(painter =
                    if (isPasswordVisible){
                        painterResource(id = R.drawable.outline_visibility_24)
                    } else {
                        painterResource(id = R.drawable.outline_visibility_off_24) // Change icons on click
                    },
                        contentDescription = "Toggle password visibility" // can add to strings.xml
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Extra Forgot password text/button thing
        Text(
            text = "Forgot your password?",
            color = Color.Gray,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable(
                onClick = navigateForgotPass,
                onClickLabel = "Navigate to forgot password" // For accessibility (?) yeah
            )
        )

        Spacer(modifier = Modifier.height(35.dp))

        // Button for Login
        Button(
            onClick = {
                scope.launch {
                    viewModel.loginUser(userEmail.value, userPassword.value)
                    viewModel.storeInfo(userEmail.value)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Login")
        }

        /**
         * Launched effects will display small Toast messages at the bottom of the screen for different states of Success or Error:
         */
        LaunchedEffect(key1 = state.value?.isSuccess) {
            scope.launch {
                if(state.value?.isSuccess?.isNotEmpty() == true) {
                    val success = state.value?.isSuccess
                    Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()
                    navigateCamera() // Navigate only to camera when successfully logged in
                }
            }
        }

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

// Preview Screen without running   emulator
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onNavigateUp = {},
        canNavigateBack = true,
        navigateForgotPass = {},
        navigateCamera = {}
    )
}