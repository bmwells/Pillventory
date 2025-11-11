package com.example.apptest.ui.settings.profilesettings

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.apptest.PillTopAppBar
import com.example.apptest.R
import com.example.apptest.ui.navigation.NavigationDestination
import kotlinx.coroutines.flow.receiveAsFlow

object ProfileSettingsDestination : NavigationDestination {
    override val route = "profile settings"
    override val titleRes = R.string.profile_title // Added to XML because this is type Int
}

// Confirm logout alert dialog
@Composable
fun LogoutConfirmationDialog(
    onConfirmLogout: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(text = stringResource(id = R.string.logout_check))
        },
        confirmButton = {
            Button(onClick = onConfirmLogout,
                colors = ButtonDefaults.buttonColors(Color.Red)
            ) {
                Text(text = stringResource(id = R.string.yes))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text(text = stringResource(id = R.string.no))
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSettingsScreen(
    navController: NavHostController,
    navigateToDeleteAccount: () -> Unit,
    navigateToLandingPage: () -> Unit,
    navigateResetPass: () -> Unit,
    viewModel: ProfileSettingsViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            PillTopAppBar(
                title = stringResource(id = ProfileSettingsDestination.titleRes),
                navigateUp = { navController.popBackStack() },
                canNavigateBack = true,
            )
        }
    ) { innerPadding ->
        ProfileSettingsBody(
            modifier = Modifier.padding(innerPadding),
            navController = navController, // Pass the navController instance
            navigateToDeleteAccount = navigateToDeleteAccount,
            navigateToLandingPage = navigateToLandingPage,
            navigateResetPass = navigateResetPass,
            viewModel = viewModel // Pass the ViewModel instance
        )
    }
}

@Composable
fun ProfileSettingsBody(
    modifier: Modifier,
    navController: NavHostController, // Add the navController parameter
    navigateToDeleteAccount: () -> Unit,
    navigateToLandingPage: () -> Unit,
    navigateResetPass: () -> Unit,
    viewModel: ProfileSettingsViewModel // Add the ViewModel parameter
) {
    val showDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current // Get the current context

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        // Display Email Address
        Text(
            text = viewModel.getUserEmail(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            textAlign = TextAlign.Center
        )

        // Reset Password Button
        Button(
            onClick = {navigateResetPass()},
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp)
        ) {
            Text(text = stringResource(id = R.string.reset_pass))
        }

        // Logout Button
        Button(
            onClick = { showDialog.value = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp)
        ) {
            Text(text = stringResource(id = R.string.logout))
        }

        // Delete Account Button
        Button(
            onClick = navigateToDeleteAccount,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp)
        ) {
            Text(text = stringResource(id = R.string.delete_acc_title)) // Add to strings xml
        }

        // Show Logout Confirmation Dialog
        if (showDialog.value) {
            LogoutConfirmationDialog(
                onConfirmLogout = {
                    // Perform logout action here
                    viewModel.logoutUser()
                },
                onDismiss = { showDialog.value = false }
            )
        }

        // Observe logout state and show toast messages based on login success
        LaunchedEffect(viewModel.logoutState) {
            viewModel.logoutState.receiveAsFlow().collect { state ->
                if (state.isSuccess != null) {
                    // Only show toast message if logout was successful
                    Toast.makeText(context, R.string.logout_s, Toast.LENGTH_LONG).show()
                    navigateToLandingPage()
                } else {
                    // Show error message if logout failed
                    Toast.makeText(context, R.string.logout_fail, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
