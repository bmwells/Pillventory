package com.example.apptest.ui.settings.help

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.apptest.PillTopAppBar
import com.example.apptest.R
import com.example.apptest.ui.navigation.NavigationDestination

object PatchNotesDestination: NavigationDestination {
    override val route = "patch_notes"
    override val titleRes = R.string.patch_notes_title
}

// Example Patch Notes
val patchNotes = listOf(
    "Version 1.2.0\n" +
            "- Feature: Added option to manually adjust pill count for greater accuracy.\n" +
            "- Bug Fix: Fixed issue causing app to crash on certain devices.",
    "Version 1.1.0\n" +
            "- Enhancement: Improved UI for better user experience.\n" +
            "- Enhancement: Added support for dark mode.",
    "Version 1.0.0\n" +
            "- Feature: Initial release of the Machine Learning Camera Pill Counting App.\n" +
            "- Feature: Ability to accurately count pills using the device's camera.\n" +
            "- Feature: Integration of machine learning algorithms to enhance counting accuracy."+
    "Version 0.9.0\n" +
            "- Enhancement: Improved pill counting accuracy.\n" +
            "- Enhancement: Optimized memory usage for smoother performance.",
    "Version 0.8.0\n" +
            "- Feature: Added pill identification feature using machine learning.\n" +
            "- Enhancement: Enhanced user interface for better usability.",
    "Version 0.7.0\n" +
            "- Feature: Introduced automatic pill recognition using the device's camera.\n" +
            "- Enhancement: Improved compatibility with various Android devices.",
    "Version 0.6.0\n" +
            "- Enhancement: Enhanced pill counting algorithm for better precision.\n" +
            "- Enhancement: Added multi-language support for a more diverse user base.",
    "Version 0.5.0\n" +
            "- Feature: Initial beta release of the Machine Learning Camera Pill Counting App.\n" +
            "- Feature: Basic pill counting functionality using the device's camera."
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatchNotesScreen(
    navController: NavHostController
) {
    Scaffold(
        topBar = {
            PillTopAppBar(
                title = stringResource(id = PatchNotesDestination.titleRes),
                navigateUp = { navController.popBackStack() },
                canNavigateBack = true,
            )
        }
    ) { innerPadding ->
        PatchNotesBody(
            modifier = Modifier.padding(innerPadding),
            navController = navController // Pass the navController instance
        )
    }
}

@Composable
fun PatchNoteItem(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        val versionNumberEndIndex = text.indexOf('\n')
        val versionNumber = text.substring(0, versionNumberEndIndex)
        val restOfText = text.substring(versionNumberEndIndex + 1)

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = buildAnnotatedString {
                    pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
                    append(versionNumber)
                    pop()
                }
            )
            Text(
                text = restOfText,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}


@Composable
fun PatchNotesBody(
    modifier: Modifier,
    navController: NavHostController // Add the navController parameter
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
    ) {
        patchNotes.forEach { note ->
            item {
                PatchNoteItem(text = note)
            }
        }
    }
}


