//package com.example.apptest.ui.pill
//
//import android.annotation.SuppressLint
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.apptest.AppTestTopAppBar
//import com.example.apptest.R
//import com.example.apptest.ui.home.HomeDestination
//import com.example.apptest.ui.navigation.NavigationDestination
//import com.example.apptest.ui.theme.AppTestTheme
//
//object PillInfoDestination : NavigationDestination {
//    override val route = "pill_info"
//    override val titleRes = R.string.get_pill
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun PillInfoScreen(
//    onNavigateUp: () -> Unit,
//    canNavigateBack: Boolean = true,
//    viewModel: PillInfoViewModel = viewModel()
//) {
//
//    val pillName = viewModel.pillName.collectAsState()
//    val pillCount = viewModel.pillCount.collectAsState()
//    val pillDescription = viewModel.pillDescription.collectAsState()
//
//    Scaffold(
//        topBar = {
//            AppTestTopAppBar(
//                title = stringResource(PillInfoDestination.titleRes),
//                canNavigateBack = canNavigateBack,
//                navigateUp = onNavigateUp
//            )
//        }
//    ) { innerPadding ->
//
//        Column(Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
//            Spacer(modifier = Modifier.height(50.dp))
//
//            OutlinedTextField(
//                value = viewModel.pillName.collectAsState().value,
//                onValueChange = { viewModel.onPillNameChange(it) },
//                label = { Text("Pill Name") },
//                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
//            )
//
//            Spacer(modifier = Modifier.height(22.dp))
//
//            Button(
//                onClick = { viewModel.readPill() },
//                modifier = Modifier
//                    .fillMaxWidth()
//            ) {
//                Text(text = "Get Pill Info")
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Text("Pill Name: ${pillName.value}")
//            Text("Pill Count: ${pillCount.value}")
//            Text("Pill Description: ${pillDescription.value}")
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
//private fun PillInfoScreenPreview() {
//    AppTestTheme {
//        PillInfoScreen(onNavigateUp = {})
//    }
//}