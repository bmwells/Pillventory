//package com.example.apptest.ui.home
//
//import android.annotation.SuppressLint
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.apptest.AppTestTopAppBar
//import com.example.apptest.R
//import com.example.apptest.ui.navigation.NavigationDestination
//import com.example.apptest.ui.pill.PillInfoScreen
//import com.example.apptest.ui.theme.AppTestTheme
//
//object HomeDestination : NavigationDestination {
//    override val route = "home"
//    override val titleRes = R.string.title
//}
//
///**
// * Entry route for Home screen
// */
//@OptIn(ExperimentalMaterial3Api::class)
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun HomeScreen(
//    navigateToPillEntry: () -> Unit,
//    navigateToPillInfo: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//
//    Scaffold(
//        modifier = modifier,
//        topBar = {
//            AppTestTopAppBar(
//                title = stringResource(HomeDestination.titleRes),
//                canNavigateBack = false
//            )
//        }
//    ) { innerPadding ->
//        HomeBody(
//            navigateToPillEntry = navigateToPillEntry,
//            navigateToPillInfo = navigateToPillInfo,
//            modifier = modifier
//        )
//
//    }
//}
//
//@Composable
//private fun HomeBody(
//    navigateToPillEntry: () -> Unit,
//    navigateToPillInfo: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = modifier
//    ) {
//
//        Spacer(modifier = Modifier.height(56.dp))
//
//        Button(
//            onClick = navigateToPillEntry,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp, vertical = 8.dp)
//        ) {
//            Text(text = "Add Pill")
//        }
//
//        Button(
//            onClick = navigateToPillInfo,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp, vertical = 8.dp)
//        ) {
//            Text(text = "Get Pill Info")
//        }
//    }
//}
//
//
//@Preview(
//    showBackground = true,
//    showSystemUi = true
//)
//@Composable
//private fun HomeScreenPreview() {
//    AppTestTheme {
//        HomeBody(navigateToPillEntry = {}, navigateToPillInfo = {})
//    }
//}