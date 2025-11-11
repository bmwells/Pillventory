//package com.example.apptest.ui.pill
//
//import android.annotation.SuppressLint
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material3.Button
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.apptest.AppTestTopAppBar
//import com.example.apptest.R
//import com.example.apptest.ReadAndWrite
//import com.example.apptest.ui.navigation.NavigationDestination
//import com.example.apptest.ui.theme.AppTestTheme
//import java.util.UUID
//
//object PillEntryDestination : NavigationDestination {
//    override val route = "pill_entry"
//    override val titleRes = R.string.add_pill
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun PillEntryScreen(
//    navigateBack: () -> Unit,
//    onNavigateUp: () -> Unit,
//    canNavigateBack: Boolean = true,
//    viewModel: PillEntryViewModel = viewModel()
//) {
//
//    val pillName = viewModel.pillName.collectAsState()
//    val pillCount = viewModel.pillCount.collectAsState()
//    val pillDescription = viewModel.pillDescription.collectAsState()
//
//    Scaffold(
//        topBar = {
//            AppTestTopAppBar(
//                title = stringResource(PillEntryDestination.titleRes),
//                canNavigateBack = canNavigateBack,
//                navigateUp = onNavigateUp
//            )
//        }
//    ) { innerPadding ->
//
//        PillInputForm(
//            onSaveClick = {
//                // Save the pill data to the Firebase Realtime Database
//                val pillId = UUID.randomUUID().toString()
//                val pill = Pill(pillName.value, pillCount.value, pillDescription.value)
//
//                ReadAndWrite().writeNewPillWithTaskListeners(
//                    pillId,
//                    pill.name!!,
//                    pill.count!!,
//                    pill.description!!
//                )
//
//                // Navigate back to the pill list screen
//                navigateBack()
//            },
//            viewModel = viewModel,
//            modifier = Modifier
//        )
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PillInputForm(
//    onSaveClick: () -> Unit,
//    viewModel: PillEntryViewModel = viewModel(),
//    modifier: Modifier = Modifier
//) {
//    Column(modifier.fillMaxSize().padding(horizontal = 16.dp)) {
//
//        Spacer(modifier = Modifier.height(66.dp))
//
//        OutlinedTextField(
//            value = viewModel.pillName.collectAsState().value,
//            onValueChange = { viewModel.onPillNameChange(it) },
//            label = { Text("Pill Name") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedTextField(
//            value = viewModel.pillCount.collectAsState().value.toString(),
//            onValueChange = {
//                viewModel.onPillCountChange(it.toIntOrNull() ?: 0)
//            },
//            label = { Text("Pill Count") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        OutlinedTextField(
//            value = viewModel.pillDescription.collectAsState().value,
//            onValueChange = { viewModel.onPillDescriptionChange(it) },
//            label = { Text("Pill Description") },
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(22.dp))
//
//        Button(
//            onClick = onSaveClick,
//            modifier = Modifier
//                .fillMaxWidth()
//
//        ) {
//            Text(text = "Save Pill")
//        }
//    }
//}
//
//
//
//@Preview(
//    showBackground = true,
//    showSystemUi = true
//)
//@Composable
//private fun PillEntryScreenPreview() {
//    AppTestTheme {
//        PillInputForm(onSaveClick = {})
//    }
//}