package com.example.apptest.ui.landing


import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.apptest.R
import com.example.apptest.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

// Defines related information to the Landing page - route and title reference
object LandingDestination : NavigationDestination {
    override val route = "landing"
    override val titleRes = R.string.landing_title
}


@Composable
fun LandingScreen(
    navigateToSignUp: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateGuest: () -> Unit,
    viewModel: LandingViewModel = hiltViewModel()
) {

    /**
     *  Variables:
     */
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val state = viewModel.guestState.collectAsState(initial = null)

    /**
     * Composable elements:
     */
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text( // Title/App name
            text = "Pillventory",
            fontSize = 40.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(110.dp)) // Adds small gaps to composable objects on screen - spacing

        // Navigate to Sign Up screen
        Button(
            onClick = navigateToSignUp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Sign Up") // Add labels to strings.xml
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigate to Login Screen
        Button(
            onClick = navigateToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Login")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Allow user to sign in as Guest/Anonymous user
        FilledTonalButton(
            onClick = {
                scope.launch {
                    viewModel.guestUse()
                }
                navigateGuest()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Continue as Guest",
                color = Color.Black
            )
        }

        /**
         * Launched effects will display small Toast messages at the bottom of the screen for different states of Success or Error:
         */
        LaunchedEffect(key1 = state.value?.isSuccess) {
            scope.launch {
                if(state.value?.isSuccess?.isNotEmpty() == true) {
                    val success = state.value?.isSuccess
                    Toast.makeText(context, "$success", Toast.LENGTH_LONG).show()

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

// Preview Screen without running emulator
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LandingScreenPreview() {
    LandingScreen(
        navigateToSignUp = {},
        navigateToLogin = {},
        navigateGuest = {}
    )
}
