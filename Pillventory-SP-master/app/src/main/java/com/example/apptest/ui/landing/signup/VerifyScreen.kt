package com.example.apptest.ui.landing.signup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.apptest.PillTopAppBar
import com.example.apptest.R
import com.example.apptest.ui.navigation.NavigationDestination

object VerifyScreenDestination : NavigationDestination {
    override val route = "verify"
    override val titleRes = R.string.verify_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerifyScreen(
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    navigateLogin : () -> Unit
) {

    Scaffold (
        topBar = {
            PillTopAppBar(
                title = stringResource(VerifyScreenDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        VerifyBody(
            navigateLogin = navigateLogin,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}

@Composable
private fun VerifyBody(
    navigateLogin : () -> Unit,
    modifier : Modifier, // Ensure body stays below the top-bar
) {

    rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Please Verify the Email you used to sign up with and then Proceed to Login",
            fontSize = 25.sp,
            color = Color.Black,
            lineHeight = 30.sp,
            textAlign = TextAlign.Center,
            modifier = modifier.padding(25.dp)
        )
    }


    Column (
        modifier = modifier.padding(32.dp)
    ) {


        Spacer(modifier = Modifier.height(250.dp))

        // Button for navigating to Login
        Button(
            onClick = {
                navigateLogin();
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Proceed To Login")
        }


    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VerifyPreview() {
    VerifyScreen(
        onNavigateUp = {},
        canNavigateBack = true,
        navigateLogin = {}
    )
}