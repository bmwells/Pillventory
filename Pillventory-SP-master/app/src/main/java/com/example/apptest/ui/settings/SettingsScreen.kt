package com.example.apptest.ui.settings


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.apptest.BottomNavigationBar
import com.example.apptest.PillTopAppBar
import com.example.apptest.R
import com.example.apptest.ui.landing.LandingScreen
import com.example.apptest.ui.navigation.NavigationDestination


object SettingsDestination: NavigationDestination {
    override val route = "settings"
    override val titleRes = R.string.settings_title // Added to XML because this is type Int
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    navigateToProfileSettings: () -> Unit,
    navigateToCameraSettings: () -> Unit,
    navigateToHelp: () -> Unit,
    navigateToContact: () -> Unit,
    navigateToBugReport: () -> Unit
) {

    Scaffold (
        topBar = {
            PillTopAppBar(
                title = stringResource(SettingsDestination.titleRes),
                canNavigateBack = false,
                navigateUp = {}
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        SettingsBody(modifier = Modifier.padding(innerPadding),
            navigateToProfileSettings = navigateToProfileSettings,
            navigateToCameraSettings = navigateToCameraSettings,
            navigateToHelp = navigateToHelp,
            navigateToContact = navigateToContact,
            navigateToBugReport = navigateToBugReport
        )

    }
}

@Composable
private fun SettingsBody(modifier: Modifier,
                         navigateToProfileSettings: () -> Unit,
                         navigateToCameraSettings: () -> Unit,
                         navigateToHelp: () -> Unit,
                         navigateToContact: () -> Unit,
                         navigateToBugReport: () -> Unit
    ) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        // PROFILE SETTINGS BUTTON
        Button(
            onClick = navigateToProfileSettings,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = stringResource(id = R.string.profile_title))
        }

        // CAMERA SETTINGS BUTTON
        Button(
            onClick = navigateToCameraSettings,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = stringResource(id = R.string.camera_settings_title))
        }

        // HELP BUTTON
        Button(
            onClick = navigateToHelp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = stringResource(id = R.string.help_title))
        }

        // CONTACT BUTTON
        Button(
            onClick = navigateToContact,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = stringResource(id = R.string.contact_title))
        }
        // BUG REPORT BUTTON
        Button(
            onClick = navigateToBugReport,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = stringResource(id = R.string.report_bug))
        }
    }
}

