package com.example.apptest.ui.settings.profilesettings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.apptest.PillTopAppBar
import com.example.apptest.R
import com.example.apptest.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object ResetPasswordDestination : NavigationDestination {
    override val route = "reset password"
    override val titleRes = R.string.reset_pass_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    onNavigateUp: () -> Unit,
    navController: NavHostController,
    navigateToProfileSettings: () -> Unit,
    canNavigateBack: Boolean = true,
) {

    Scaffold (
        topBar = {
            PillTopAppBar(
                title = stringResource(ResetPasswordDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        ResetPasswordBody(
            navController = navController,
            navigateToProfileSettings =  navigateToProfileSettings,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }

}

@Composable
private fun ResetPasswordBody(
    viewModel : ResetPasswordViewModel = hiltViewModel(),
    navigateToProfileSettings: () -> Unit,
    navController: NavHostController,
    modifier : Modifier, // Ensure body stays below the top-bar
) {

    val userEmailforPass = viewModel.userEmailforPass.collectAsState()

    val scope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Please Enter Email Confirmation to send reset link",
            fontSize = 25.sp,
            color = Color.DarkGray,
            lineHeight = 30.sp,
            textAlign = TextAlign.Center
        )
    }


    Column (
        modifier = modifier.padding(32.dp)
    ) {


        Spacer(modifier = Modifier.height(50.dp))

        // Text box for Enter email address
        OutlinedTextField(
            value = viewModel.userEmailforPass.collectAsState().value,
            onValueChange = { viewModel.onEmailChange(it) }, // Update value seen as user types it
            label = { Text(text = "Enter email address") }, // Add text to strings.xml
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(8.dp))

        Spacer(modifier = Modifier.height(75.dp))

        // Button for sending verification
        Button(
            onClick = {
                scope.launch {
                    viewModel.sendVerification(userEmailforPass.value);
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Send Reset Link")
        }
    }
}