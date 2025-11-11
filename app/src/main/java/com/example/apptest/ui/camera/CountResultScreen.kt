package com.example.apptest.ui.camera


import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.apptest.PillTopAppBar
import com.example.apptest.R
import com.example.apptest.ui.navigation.NavigationDestination
import com.example.pillsv.camera.CountResultViewModel

// Defines related information to the Count Results page - route and title reference
object CountResultDestination: NavigationDestination {
    override val route = "countresults"
    override val titleRes = R.string.camera_title //  Keeping the Camera title for continuity
    const val countArg = "count"
    val routeWithArgs = "$route/{$countArg}"
}

// Screen to show the count for the pill record
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountResultsScreen(
    navigateCamera: () -> Unit,
    navigateAddTags: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CountResultViewModel = hiltViewModel()
) {
    /**
     * Need to distinguish between account users and guest users
     */
    val isGuest = viewModel.isUserGuest()

    /**
     * Modal variables
     */
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Scaffold (
        topBar = {
            PillTopAppBar(
                title = stringResource(CameraDestination.titleRes),
                canNavigateBack = false,
                navigateUp = {} // No navigation backward needed
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(text = "Pill Count: " + viewModel.getCount()) }, // Get current count number concatenated
                icon = {}, // No icon necessary
                onClick = {
                    showBottomSheet = !isGuest // Show the bottom sheet if user is not a guest
                    if(isGuest){
                        Toast.makeText(context, "Expansion locked for Guest", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp) // Needed to have even padding on left and right sides
            )
        }
    ) { innerPadding ->
        Box ( // Box composable is used to layer elements
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ){
            // Background image
            viewModel.getImageBitmap()?.let {
            //viewModel.imageFromProc.collectAsState().value?.let {
            Image(
                //painter = painterResource(id = R.drawable.test_image), // Image placeholder until app can get image taken/uploaded
                bitmap = it.asImageBitmap(),
                contentDescription = "Image processed background",
                contentScale = ContentScale.FillBounds,
                modifier = modifier
                    .matchParentSize()
                    .align(Alignment.Center)
            )
        }
            // 'X' or close button
            IconButton(onClick = navigateCamera, modifier = Modifier
                .size(60.dp)
                .padding(16.dp)) { // Navigate back to camera when page is closed out
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close page icon",
                    tint = Color.White,
                    modifier = Modifier.size(60.dp)
                )
            }

            // Bottom pop up for verified users to access
            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { // Close Modal when slide down
                        showBottomSheet = false
                    },
                    sheetState = sheetState,
                    modifier = modifier.fillMaxHeight() // Allow the sheet to expand to full device height
                ) {
                    // Sheet content
                    ResultsContents(
                        modifier = Modifier.padding(innerPadding),
                        navigateCamera = navigateCamera,
                        navigateAddTags = navigateAddTags
                    )
                }

                // Avoid overlapping the system navigation bar by adding a space over the height
                Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            }
        }
    }
}


// Content located inside the Modal pop up sheet- pill count, date, tags, description
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
private fun ResultsContents(
    navigateCamera: () -> Unit,
    navigateAddTags: () -> Unit,
    modifier : Modifier = Modifier,
    viewModel: CountResultViewModel = hiltViewModel()
) {
    /**
     * Variables
     */
    val maxCharLength = 50
    val isOverMaxLength = viewModel.isOverMaxLength.collectAsState()

    /**
     * Composable elements
     */
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text( // Display the pill count
            text = "Pill Count: " + viewModel.getCount(),
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(9.dp)) // Show small gap between elements

        Text( // Current date
            text = "Date: " + viewModel.getDate(),
            fontSize = 16.sp,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(9.dp))

        Row(){
            IconButton( // TO BE IMPLEMENTED LATER
                onClick = navigateAddTags
            ) {
                Icon(
                    painter = painterResource(R.drawable.outline_bookmark_add_24),
                    contentDescription = "Add tags",
                    modifier = Modifier.background(color = Color.LightGray, shape = CircleShape)
                )
            }

            // Need to implement some list of tag objects after tags added *
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Description text field
        TextField(
            value = viewModel.description.collectAsState().value,
            onValueChange = {
                if(it.length <= maxCharLength){
                    viewModel.onDescriptionChange(it);
                } else {
                    viewModel.onDescOverLength();
                }},
            label = { Text(text = "Description text field") },
            singleLine = false,
            maxLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        // Warning message popup to notify users they are using too many characters
        if(isOverMaxLength.value) {
            Text(text = "Too many characters", fontWeight = FontWeight.Medium, color = Color.Red)
        }
        if(viewModel.description.value.length < maxCharLength){
            Text(text = "")
            viewModel.onDescUnderLength();
        }

        Spacer(modifier = Modifier.height(15.dp))

        // Save button
        Button(
            onClick = navigateCamera, // Will navigate to camera but also needs to save value to Realtime Database
            modifier = Modifier.fillMaxWidth()
        ) { // TO BE IMPLEMENTED LATER
            Text(text = "Save",fontSize = 16.sp)
        }
    }
}

// Preview screen elements
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CountResultsScreenPreview() {
    CountResultsScreen(
        navigateCamera = {},
        navigateAddTags = {}
    )
    //ResultsContents()
}