package com.example.apptest.ui.settings.contact

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.apptest.PillTopAppBar
import com.example.apptest.R
import com.example.apptest.ui.navigation.NavigationDestination

object BugReportDestination: NavigationDestination {
    override val route = "bug_report"
    override val titleRes = R.string.bug_report // Added to XML because this is type Int
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BugReportScreen(
    navController: NavHostController
) {
    // State variable to track if the email has been sent
    var emailSent by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            PillTopAppBar(
                title = stringResource(id = BugReportDestination.titleRes),
                navigateUp = { navController.popBackStack() },
                canNavigateBack = true,
            )
        }
    ) { innerPadding ->
        BugReportBody(
            modifier = Modifier.padding(innerPadding),
            onSendClick = { email, subject, message ->
                // Send Email
                sendEmail(context, email, subject, message)
                // Pop back to settings screen
                emailSent = true
            },
            navController = navController,
            emailSent = emailSent
        )
    }
}

@Composable
fun BugReportBody(
    onSendClick: (email: String, subject: String, message: String) -> Unit,
    modifier: Modifier,
    navController: NavHostController,
    emailSent: Boolean
) {
    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var canSend by remember { mutableStateOf(false) }

    // Dropdown items for subject selection
    val subjectOptions = listOf(
        stringResource(id = R.string.bug1),
        stringResource(id = R.string.bug2),
        stringResource(id = R.string.bug3),
        stringResource(id = R.string.bug4),
        stringResource(id = R.string.bug5),
        stringResource(id = R.string.bug6),
        stringResource(id = R.string.bug7)
    )

    var expanded by remember { mutableStateOf(false) }

    // Side effect to trigger navigation when email is sent
    LaunchedEffect(emailSent) {
        if (emailSent) {
            navController.popBackStack()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        // Subject Dropdown Menu
        Box(
            modifier = Modifier.fillMaxWidth()
                .border(width = 1.dp, color = Color.Black, shape = MaterialTheme.shapes.small)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 12.dp)
                    .clickable { expanded = true }
            ) {
                Text(
                    text = if (subject.isNotBlank()) subject else stringResource(id = R.string.bug_type),
                    modifier = Modifier.weight(1f) // Adjust the weight to control text and icon alignment
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null, // Provide a meaningful description if needed
                    tint = Color.Black, // Adjust the color of the icon as needed
                    modifier = Modifier.size(24.dp) // Adjust the size of the icon as needed
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                subjectOptions.forEach { option ->
                    DropdownMenuItem(onClick = {
                        subject = option
                        expanded = false
                        canSend = email.isNotBlank() && message.isNotBlank()
                    }) {
                        Text(text = option)
                    }
                }
            }
        }

        // Email Text Field
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                canSend = email.isNotBlank() && message.isNotBlank()
            },
            label = { Text(text = stringResource(id = R.string.enter_email))},
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        // Message Text Field
        OutlinedTextField(
            value = message,
            onValueChange = {
                message = it
                canSend = email.isNotBlank() && message.isNotBlank()
            },
            label = { Text(text = stringResource(id = R.string.enter_msg))},
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp) // Larger height for the last text field
                .padding(vertical = 8.dp)
        )

        // Send Button
        Button(
            onClick = {
                // Handle sending the email
                onSendClick(email, subject, message)
            },
            enabled = email.isNotBlank() && message.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(text = stringResource(id = R.string.send))
        }
    }
}







