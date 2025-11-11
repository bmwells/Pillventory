package com.example.apptest.ui.landing.guest

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Screen elements for Guest user - will reroute guest users when they reach a page they cannot access
@Composable
fun GuestRerouteScreen(
    navigateSignIn: () -> Unit ,
    navigateLogin: () -> Unit,
    modifier : Modifier = Modifier
) {

    Column ( // Column to stack composable elements
        modifier = modifier.padding(16.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text( // Header text
            text = "Sign up with Pillventory to access.",
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(50.dp))

        // Button to navigate to create an account/register user screen
        Button(onClick = navigateSignIn) {
            Text(
                text = "Create a free account",
                fontSize = 17.sp,
            )
        }

        Spacer(modifier = Modifier.height(5.dp))

        // Clickable text for user logging in
        Text(
            text = "Already have an account? Login.",
            fontSize = 16.sp,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable(
                onClick = navigateLogin,
                onClickLabel = "Navigate to login" // For accessibility
            )
        )
    }
}

// Preview screen elements
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GuestRerouteScreenPreview() {
    GuestRerouteScreen(
        navigateSignIn = {},
        navigateLogin = {}
    )
}