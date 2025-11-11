package com.example.apptest.ui.settings.help

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.apptest.PillTopAppBar
import com.example.apptest.R
import com.example.apptest.ui.navigation.NavigationDestination



object HelpDestination: NavigationDestination {
    override val route = "help"
    override val titleRes = R.string.help_title // Added to XML because this is type Int
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    navController: NavHostController,
    navigateToPatchNotes: () -> Unit,
) {
    Scaffold(
        topBar = {
            PillTopAppBar(
                title = stringResource(id = HelpDestination.titleRes),
                navigateUp = { navController.popBackStack() },
                canNavigateBack = true,
            )
        }
    ) { innerPadding ->
        HelpBody(
            modifier = Modifier.padding(innerPadding),
            navigateToPatchNotes = navigateToPatchNotes,
            navController = navController // Pass the navController instance
        )
    }
}

@Composable
fun HelpBody(
    modifier: Modifier,
    navigateToPatchNotes: () -> Unit,
    navController: NavHostController,

) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {

        // FQA
        Text(
            text = stringResource(id = R.string.faq),
            modifier = Modifier
                .fillMaxWidth()
                .offset((-70).dp, (20).dp),
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontSize = 16.sp,
            //fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.SemiBold,
            //textDecoration = TextDecoration.Underline
        )
        QuestionBox(
            question = stringResource(R.string.q1), answer = stringResource(R.string.a1),
            modifier = modifier.offset((0).dp, (-20).dp))
        QuestionBox(
            question = stringResource(R.string.q2), answer = stringResource(R.string.a2),
            modifier = modifier.offset((0).dp, (-80).dp))

        QuestionBox(
            question = stringResource(R.string.q3), answer = stringResource(R.string.a3),
            modifier = modifier.offset((0).dp, (-140).dp))

        QuestionBox(
            question = stringResource(R.string.q4), answer = stringResource(R.string.a4),
            modifier = modifier.offset((0).dp, (-200).dp))

        QuestionBox(
            question = stringResource(R.string.q5), answer = stringResource(R.string.a5),
            modifier = modifier.offset((0).dp, (-260).dp))

        // Patch Notes Button
        Button(
            onClick = navigateToPatchNotes,
            modifier = Modifier
                .offset((0).dp, (-150).dp)
                .fillMaxWidth()
                .padding(horizontal = 75.dp)
                .padding(bottom = 16.dp), // Adjust bottom padding to maintain spacing
        ) {
            Text(text = stringResource(id = R.string.patch_notes))
        }

        // Camera tutorial Button
//        Button(
//            onClick = {
//                // Youtube video link
//            },
//            modifier = Modifier
//                .offset((0).dp, (-150).dp)
//                .fillMaxWidth()
//                .padding(horizontal = 75.dp)
//                .padding(bottom = 16.dp), // Adjust bottom padding to maintain spacing
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(text = stringResource(id = R.string.tutorial),
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier
//                        .padding(end = 16.dp))
//                Icon(
//                    painter = painterResource(id = R.drawable.camera_tutorial),
//                    contentDescription = "Camera Tutorial",
//                    modifier = Modifier.size(24.dp) // Adjust icon size as needed
//                )
//            }
//        }
    }
}

@Composable
fun QuestionBox(question: String, answer: String, modifier: Modifier) {
    var expanded by remember { mutableStateOf(false)}

    Card(
        modifier = modifier
            .padding(horizontal = 25.dp) // Adjust padding to add space on both sides
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(8.dp)
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessVeryLow
                    )
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = question, fontSize = 15.sp, modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand/Collapse",
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            }
            if (expanded) {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = answer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(
                            start = 16.dp,
                            top = 8.dp,
                            bottom = 16.dp,
                            end = 2.dp
                        )
                    )
                }
            }
        }
    }
}