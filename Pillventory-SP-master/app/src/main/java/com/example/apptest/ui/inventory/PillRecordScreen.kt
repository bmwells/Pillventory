package com.example.apptest.ui.inventory

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.apptest.PillTopAppBar
import com.example.apptest.R
import com.example.apptest.ui.inventory.cards.PillImage
import com.example.apptest.ui.navigation.NavigationDestination

// Defines related information to the Inventory page - route and title reference
object RecordDestination: NavigationDestination {
    override val route = "pillrecord"
    override val titleRes = R.string.inventory_title // Added to XML because this is type Int
    const val itemArg = "itemId"
    val routeWithArgs = "$route/{$itemArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PillRecordScreen(
    onNavigateUp: () -> Unit,
    navigateAddTagScreen: () -> Unit
) {
    Scaffold ( // Used to define the top bar
        topBar = {
            PillTopAppBar(
                title = stringResource(RecordDestination.titleRes), // Display the title reference in the top bar
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        PillRecordBody(Modifier.padding(innerPadding),
            onNavigateUp = onNavigateUp,
            navigateAddTagScreen = navigateAddTagScreen)

    }
}


// Body will be called in screen
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun PillRecordBody(
    modifier: Modifier = Modifier,
    onNavigateUp: () -> Unit,
    viewModel: PillRecordViewModel = hiltViewModel(),
    navigateAddTagScreen: () -> Unit
) {
    /**
     * Fetch the pill record for the respective pill record
     */
    LaunchedEffect(viewModel) {
        viewModel.fetchIndividualPillRecord()
    }

    // Related variables
    var isEditing by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val pillCount = viewModel.pillCount.collectAsState()
    val pillDescription = viewModel.description.collectAsState()

    val context = LocalContext.current

    if (isEditing) {

        // Keep track of the original values in case the user cancels the Edit
        val originalCount = viewModel.getOriginalPillCount()
        val originalDescription = viewModel.getOriginalDescription()
        val changeableTags = viewModel.getTagsList()

        // Record the values when changed to update the database
        var countText by remember { mutableStateOf(originalCount) }
        var descText by remember { mutableStateOf(originalDescription) }

        // Keep track if text has changes from its original values to enable the save button
        val isTextChanged = (countText != originalCount) or (descText != originalDescription)

        Column(
            modifier = modifier
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray)
                    .height(350.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PillImage(viewModel.getImageRef())
            }

            Spacer(modifier = Modifier.height(2.dp))

            Column(
                modifier = Modifier.padding(start = 16.dp, end= 16.dp)
            ) {
                Column (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    IconButton(
                        onClick = {
                            isEditing = false
                            // Restore original values
                            viewModel.onPillCountChange(originalCount)
                            viewModel.onDescriptionChange(originalDescription)
                        }
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel edit")
                    }
                }

                TextField(
                    value = viewModel.pillCount.collectAsState().value,
                    onValueChange = { viewModel.onPillCountChange(it); countText = it },
                    label = { Text("Edit Pill Count") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))
                TextField(
                    value = viewModel.description.collectAsState().value,
                    onValueChange = {viewModel.onDescriptionChange(it); descText = it },
                    label = { Text("Edit Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Row{
                    IconButton(onClick = {
                        navigateAddTagScreen();
                        viewModel.passItemRecordIdToRepository();
                    }) { // Navigate to the add tags page ..
                        Icon(
                            painter = painterResource(id = R.drawable.outline_bookmark_add_24),
                            contentDescription = "Add tags",
                           // modifier = Modifier.background(color = Color.LightGray, shape = CircleShape)
                        )
                    }
                    changeableTags.forEach { tagName ->
                        AssistChip(
                            onClick = {
                                Log.d("Tag name", "click deactivated")
                                changeableTags.minus(tagName)
                            },
                            label = { Text(text = tagName) },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null
                                )
                            }
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                }

                Spacer(modifier = Modifier.height(7.dp))
                Row (
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Button is only enabled/clickable if the information in the record is altered
                    Button(
                        onClick = {
                            viewModel.updateRecordUI()
                            isEditing = false
                        },
                        enabled = isTextChanged,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Save Changes")
                    }
                }
            }
        }
    } else {
        Column (modifier = modifier) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray)
                    .height(350.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PillImage(viewModel.getImageRef())
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)){
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Pill Count: " + viewModel.getPillCount(), fontSize = 20.sp, textAlign = TextAlign.Justify)

                    Spacer(modifier = Modifier.weight(1f))

                    IconButtonWithDropdownMenu(
                        onEditClicked = { isEditing = true },
                        onDeleteClicked = { showDialog = true },
                        onShareClicked = { viewModel.ShareRecord(context) }
                    )
                }

                Text(text = viewModel.getDate())

                Spacer(modifier = Modifier.height(5.dp))

                // Displaying tags side by side with row composable
                Row {
                    viewModel.getTagsList().forEach {tagName ->
                        AssistChip(
                            onClick = { Log.d("Tag name", "click deactivated") },
                            label = { Text(text = tagName) },
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))

                Text(text = pillDescription.value, fontSize = 16.sp) // Do not change this line

            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                // Handle when the dialog is dismissed (e.g., when user clicks outside the dialog)
                showDialog = false
            },
            title = { Text(text = "Confirm Delete") },
            text = { Text("Are you sure you want to delete this record?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePillRecord()
                        showDialog = false
                        onNavigateUp()
                    }
                ) {
                    Text("Delete") // User chooses to delete the record
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text("Cancel") // User cancels choice - does NOT delete
                }
            }
        )
    }
}


@Composable
fun IconButtonWithDropdownMenu(
    onEditClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onShareClicked: () -> Unit
) {
    val expanded = remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded.value = true }) {
            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More options")
        }

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier.width(200.dp)
        ) {
            DropdownMenuItem(
                leadingIcon = { Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit option" )},
                text = {Text(text = "Edit")},
                onClick = { onEditClicked(); expanded.value = false }
            )
            DropdownMenuItem(
                leadingIcon = { Icon(imageVector = Icons.Default.Share, contentDescription = "Share option" )},
                text = {Text(text = "Share")},
                onClick = { onShareClicked(); expanded.value = false }
            )
            DropdownMenuItem(
                leadingIcon = { Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete option" )},
                text = {Text(text = "Delete")},
                onClick = { onDeleteClicked(); expanded.value = false }
            )
        }
    }
}

//@Composable
//@Preview(showBackground = true, showSystemUi = true)
//fun PreviewRecordScreen(){
//    PillRecordBody()
//}
