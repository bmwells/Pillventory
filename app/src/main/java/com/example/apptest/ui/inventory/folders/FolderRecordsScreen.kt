package com.example.apptest.ui.inventory.folders

//import androidx.compose.material.Button
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.apptest.PillTopAppBar
import com.example.apptest.R
import com.example.apptest.ui.inventory.calculator.ShortenedPillRecord
import com.example.apptest.ui.inventory.cards.PillImage
import com.example.apptest.ui.inventory.cards.RecordCard
import com.example.apptest.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

// Defines related information to the Folder records page - route and title reference
object FolderRecordsDestination: NavigationDestination {
    override val route = "folderrecords"
    override val titleRes = R.string.folder_title
    const val folderArg = "folderId" // may need to pass a second val for owner id
    const val userArg = "ownerId"
    val routeWithArgs = "$route/{$folderArg}/{$userArg}"
}

// Composable record screen itself for showing the folder's records/contents
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderRecordsScreen(
    onNavigateUp: () -> Unit,
    viewModel: FolderRecordsViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel) {
        viewModel.fetchFolderInfo()
    }

    // variables
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val shareFolderSheetState = rememberModalBottomSheetState()
    var showShareFolderSheet by remember { mutableStateOf(false) }

    Scaffold ( // Used to define the top bar
        topBar = {
            PillTopAppBar(
                title = viewModel.getFolderName(), // Display the title reference in the top bar
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        },
        floatingActionButton = { // floating action button for user to adjust the contents of the folder later
            FloatingActionButton(
                onClick = {showBottomSheet = true }
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Folder content")
            }
        }
    ) { innerPadding ->

        // Bottom sheet for folder editing enabled when user clicks floating action button
        if(showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState,
                modifier = Modifier.fillMaxHeight()
            ) {
                RecordCheckBoxSelection(onCloseBottomSheet = { showBottomSheet = false })
            }
        }

        // Bottom sheet for sharing folder contents when user clicks share icon
        if(showShareFolderSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showShareFolderSheet = false
                },
                sheetState = shareFolderSheetState,
                modifier = Modifier.fillMaxHeight()
            ) {
                ShareFolderBody(onCloseBottomSheet = {showShareFolderSheet = false})
            }
        }

        // Defaulted content for displaying folder records if any exist

        Column (modifier = Modifier.padding(innerPadding)) {

            Row (modifier = Modifier.padding(start = 16.dp, end = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                // Folder description
                Text(text = viewModel.getFolderDescription(), maxLines = 2)

                Spacer(Modifier.weight(1f))

                // Share button
                IconButton(onClick = { showShareFolderSheet = true }) {
                    Icon(painter = painterResource(R.drawable.baseline_person_add_alt_24), contentDescription = "Add coworker icon")
                }
            }

            // Records if anys
            LazyColumn (modifier = Modifier.padding(start = 16.dp, end = 16.dp)){
                if(viewModel.folderPillRecords.isNotEmpty()) {
                    items(viewModel.folderPillRecords) { record ->
                        //Text("Pill ID: ${record.pillId}") // Check for individual unique record
                        RecordCard(
                            record.pillId,
                            record.count,
                            record.date,
                            record.description,
                            record.tags,
                            record.imageRef,
                            navigateRecord = {} // records will not be edited or deleted - only in inventory
                        )

                    }
                } else { // fail safe to call getFolderRecords if timing of coroutines fails the first time
                    for( i in viewModel.getFolderRecordIds() ) {
                        println(i)
                    }
                }
            }
        }
    }
}

// Composable for user to fill out if they wish to share folder content with another Pillventory user
@Composable
fun ShareFolderBody(
    viewModel: FolderRecordsViewModel = hiltViewModel(),
    onCloseBottomSheet: () -> Unit
) {

    val shareWithEmail = viewModel.shareWithEmail.collectAsState()
    val shareWithId = viewModel.shareWithEmailUUID.collectAsState()
    val userIdSharedState by viewModel.userIdShared.collectAsState()
    val scope = rememberCoroutineScope()

    Column (modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
        Text(text = "Share folder", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.CenterHorizontally))

        Spacer(modifier = Modifier.height(16.dp))

        // Text field for folder name input
        OutlinedTextField(
            value = viewModel.shareWithEmail.collectAsState().value,
            onValueChange = { viewModel.onEmailChange(it) }, // Update value seen as user types it
            label = { Text(text = "Share with Pillventory account email")}, // Add text to strings.xml
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(13.dp))

        Button(
            onClick = {
                // Get UUID from email -> shareWithUserID
                scope.launch {
//                    viewModel.fetchValueByEmail(shareWithEmail.value) {
//                        viewModel.setSharedUserId(it)
//                    }
                    viewModel.fetchUserId(shareWithEmail.value)
                    viewModel.shareFolderContentLocal()
                    viewModel.shareFolderContentOtherUser()
                }
                // Get UUID from current user -> originalOwnerID
                // Get UUID from folder -> folderID

                onCloseBottomSheet()
                // Share pathway:
                viewModel.onEmailChange("") // Clear the text box
            },
            enabled = viewModel.shareWithEmail.collectAsState().value.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Share")
        }

        /** Test if other user recipient id is properly fetched
         *  Passed - prints out correctly
         */
        if(userIdSharedState != null) {
            Text(text = userIdSharedState.toString())

            viewModel.shareFolderContentOtherUser()
        }
        //Text(text = shareWithId.value)
    }
}


// List boxes composable - used for adding records to folder
@Composable
fun RecordCheckBoxSelection(
    viewModel: FolderRecordsViewModel = hiltViewModel(),
    onCloseBottomSheet: () -> Unit
) {
    LaunchedEffect(viewModel) {
        viewModel.fetchShortenedPills()
    }
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ){
        Text(text = "Manage Folder Content",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .padding(start = 16.dp, end = 16.dp)
        )

        Spacer(Modifier.height(20.dp))

        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Button(
                onClick = {
                    viewModel.updateFolderWithSelectedPillIds()
                    viewModel.fetchFolderInfo()
                    onCloseBottomSheet()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Update Folder")
            }

        }

        Spacer(Modifier.height(7.dp))

        Box {
            Column {
                LazyColumn {
                    items(viewModel.shortenedPillRecords) {record ->
                        FolderListItem(
                            record.pillId,
                            record.count,
                            record.date,
                            record.imageRef,
                            record ,
                            isChecked = viewModel.isPillInFolder(record.pillId),
                            onCheckedChange = {
                                viewModel.toggleItemSelection(record.pillId)
                            }
                        )
                    }
                }
            }

        }
    }
}

// Composable for displaying relevant information of the pill record within the folder
@Composable
fun FolderListItem(
    pillId: String,
    pillCount: String,
    date: String,
    imageRef: String,
    record: ShortenedPillRecord,
    isChecked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit
) {

    val checkedState = remember { mutableStateOf(isChecked) }

    ListItem(
        headlineContent = {
            Text(text = "Pill Count: $pillCount") // Display the pill count
        },
        supportingContent = {
            Text(text = date) // Display the related date of the record
        },
        leadingContent = {
            PillImage(imageUrl = imageRef) // Helpful image to assist in counting
        },
        trailingContent = {
            Checkbox(
                checked = checkedState.value,
                onCheckedChange = {
                    checkedState.value = it; // When the check box is checked/unchecked change its state
                    onCheckedChange(it);
                }
            )
        },
        modifier = Modifier.height(65.dp)

    )
    Divider(color = Color.LightGray, modifier = Modifier.padding(start= 16.dp, end =16.dp)) // Divider to separate the records in the loop

}

