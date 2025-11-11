package com.example.apptest.ui.inventory.folders

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.apptest.InventoryTopAppBar
import com.example.apptest.R
import com.example.apptest.ui.inventory.InventoryDestination
import com.example.apptest.ui.inventory.InventoryItem
import com.example.apptest.ui.inventory.alarm.AlarmScreenDestination
import com.example.apptest.ui.inventory.calculator.CalculatorDestination
import com.example.apptest.ui.inventory.metrics.MetricsScreenDestination
import com.example.apptest.ui.landing.tagmanagement.TagScreeDestination
import com.example.apptest.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch


// Defines related information to the Folder page - route and title reference
object FolderDestination: NavigationDestination {
    override val route = "folders"
    override val titleRes = R.string.folder_title // Added to XML because this is type Int
}

// Folder screen overall Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderScreen(
    navController: NavHostController,
    navigateFolder: (String, String) -> Unit
) {

    /**
     * Variables
     */
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItemIndex by rememberSaveable {
        mutableStateOf(2) // The selected index has to be the index of the inventoryItem for this screen for the highlight to line up correctly. If zero it will default to the inventory home view being highlighted
    }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    // Define list of items in the side menu (Calculator for relevancy)
    val items = listOf(
        // Will define routes later (when pages are created) since theres no references to routes now

        InventoryItem(// 0
            title = "Inventory View",
            selectedIcon = painterResource(R.drawable.baseline_photo_library_24),
            unselectedIcon = painterResource(R.drawable.outline_photo_library_24),
            navigationDestination = InventoryDestination
        ),
        InventoryItem( // 1
            title = "Tag Management",
            selectedIcon = painterResource(R.drawable.baseline_bookmark_24),
            unselectedIcon = painterResource(R.drawable.baseline_bookmark_border_24),
            navigationDestination = TagScreeDestination
        ),
        InventoryItem(//2
            title = "Folders",
            selectedIcon = painterResource(R.drawable.baseline_folder_24),
            unselectedIcon = painterResource(R.drawable.baseline_folder_open_24),
            navigationDestination = FolderDestination
        ),

        InventoryItem(//3
            title = "Calculator",
            selectedIcon = painterResource(R.drawable.baseline_calculate_24),
            unselectedIcon = painterResource(R.drawable.outline_calculate_24),
            navigationDestination = CalculatorDestination
        ),
        InventoryItem(//4
            title = "Set Alarm",
            selectedIcon = painterResource(R.drawable.baseline_timer_24),
            unselectedIcon = painterResource(R.drawable.outline_timer_24),
            navigationDestination = AlarmScreenDestination
        ),
        InventoryItem( // 5
            title = "Metrics",
            selectedIcon = painterResource(R.drawable.baseline_metrics_24),
            unselectedIcon = painterResource(R.drawable.metrics_outline_24),
            navigationDestination = MetricsScreenDestination
        )
    )

    // Modal navigation composable
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))

                items.forEachIndexed { index, inventoryItem ->
                    NavigationDrawerItem(
                        label = { Text(text = inventoryItem.title) },
                        selected = index == selectedItemIndex,
                        onClick = {
                            selectedItemIndex = index
                            scope.launch {
                                drawerState.close()
                                navController.navigate(inventoryItem.navigationDestination.route)
                            }

                        },
                        icon = {
                            Icon(
                                painter = if (index == selectedItemIndex) { // When an item is selected it's icon will change to show response
                                    inventoryItem.selectedIcon
                                } else inventoryItem.unselectedIcon,
                                contentDescription = inventoryItem.title
                            )
                        },
                        modifier = Modifier
                            .padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
    ) {
        Scaffold(
            topBar = {
                InventoryTopAppBar(
                    // Top bar will be adjusted for Tag Management Button, Calculator, and Files/Folders etc.
                    title = stringResource(FolderDestination.titleRes),
                    //moreOptions = true,
                    menuClick = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    text = { Text(text = "New Folder") },
                    icon = { Icon(painter = painterResource(id = R.drawable.baseline_add_24), contentDescription = "Add icon")},
                    onClick = {showBottomSheet = true }
                )
            }
        ) { innerPadding ->

            var state by remember {mutableStateOf(0)}
            val titles = listOf("Personal", "Shared")

            Column(modifier = Modifier.padding(innerPadding)) {
                TabRow(selectedTabIndex = state) {
                    titles.forEachIndexed { index, title ->
                        Tab(
                            selected = state == index,
                            onClick = { state = index },
                            text = {Text(text = title, overflow = TextOverflow.Ellipsis)}
                        )
                    }
                }

                if(state == 0) {
                    if(showBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = {
                                showBottomSheet = false
                            },
                            sheetState = sheetState,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            AddFolderBody(onCloseBottomSheet = { showBottomSheet = false })
                        }
                    }
                    FolderBody(
                        navigateFolder,
                        modifier = Modifier.padding(innerPadding)
                    )
                } else if (state == 1) {
                    SharedFolderBody(
                        navigateFolder,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// Tab section for displaying the shared folders in the UI
@Composable
fun SharedFolderBody(
    navigateFolder: (String, String) -> Unit,
    viewModel: FolderViewModel = hiltViewModel(),
    modifier: Modifier
) {
    LaunchedEffect(viewModel) { // retrieve any shared folders, blank if none
        viewModel.fetchSharedFolderInfo()
    }

    Column (modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)) {
        LazyColumn {
            if(viewModel.pillFoldersShared.isNotEmpty()) {
                items(viewModel.pillFoldersShared) {folder -> // iterate through each folder
                    FolderCardRevised(
                        folder.folderId,
                        folder.folderName,
                        folder.folderDescription,
                        folder.folderQuantity,
                        folder.folderRecords,
                        navigateFolder = navigateFolder,
                    )
                }
            }
        }
    }
}

// Tab section for displaying the personal created folders by the user in the UI
@Composable
fun FolderBody(
    navigateFolder: (String, String) -> Unit,
    viewModel: FolderViewModel = hiltViewModel(),
    modifier: Modifier
) {
    LaunchedEffect(viewModel) { // fetch folders
        viewModel.fetchFolders()
    }

    Column (modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)) {
        LazyColumn {
            items(viewModel.pillFolders) {folder -> // iterate through each folder
                FolderCardRevised(
                    folder.folderId,
                    folder.folderName,
                    folder.folderDescription,
                    folder.folderQuantity,
                    folder.folderRecords,
                    navigateFolder = navigateFolder,
                )
            }
        }
    }
}

// Composable object for each folder listed with logical folder icon
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderCardRevised(
    folderId: String,
    folderName: String,
    folderDescription: String,
    folderQuantity: String,
    folderRecords: String,
    navigateFolder: (String, String) -> Unit,
    viewModel: FolderViewModel = hiltViewModel()
) {

    Card(
        onClick = { viewModel.getOwnerIdByFolderId(folderId)?.let { navigateFolder(folderId, it) } } , // Needs to be implemented
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth()
        // .padding(16.dp)
    ) {
        Row (modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.baseline_folder_open_24),
                contentDescription = "Folder icon"
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(text = folderName, fontSize = 18.sp)

            Spacer(modifier = Modifier.weight(1f))

            // Want to display quantity of folder to preview for users
            Text(text = folderQuantity, fontSize = 17.sp, color = Color.DarkGray)
        }

    }
    Spacer(modifier = Modifier.height(13.dp))
}

// Composable for user to create a new folder with name and optional description
@Composable
fun AddFolderBody(
    viewModel: FolderViewModel = hiltViewModel(),
    onCloseBottomSheet: () -> Unit // Callback function to close bottom sheet
) {

    val folderName = viewModel.folderName.collectAsState()
    val folderDescription = viewModel.folderDescription.collectAsState()

    val scope = rememberCoroutineScope()

    Column (
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = "Create a new folder", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.CenterHorizontally))

        Spacer(modifier = Modifier.height(16.dp))

        // Text field for folder name input
        OutlinedTextField(
            value = viewModel.folderName.collectAsState().value,
            onValueChange = { viewModel.onNameChange(it) }, // Update value seen as user types it
            label = { Text(text = "Enter folder name")}, // Add text to strings.xml
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(13.dp))

        // Text field for folder description input
        OutlinedTextField(
            value = viewModel.folderDescription.collectAsState().value,
            onValueChange = { viewModel.onDescriptionChange(it) }, // Update value seen as user types it
            label = { Text(text = "Enter description (optional)")}, // Add text to strings.xml
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(13.dp))

        // Button for creating folder
        Button(
            onClick = {
                scope.launch {
                    viewModel.createFolder(folderName.value, folderDescription.value)
                }
                onCloseBottomSheet()
                // Clear the text boxes
                viewModel.onNameChange("")
                viewModel.onDescriptionChange("")
            },
            enabled = viewModel.folderName.collectAsState().value.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Create Folder")
        }
    }
}

// Test for list items for a folder - shown in Preview()
@Composable
fun FolderItem(
    title: String,
    quantity: String
){
    val checkedState = remember { mutableStateOf(false) }

    ListItem(
        headlineContent = {
            Text(text = title) // Display the pill count
        },
        leadingContent = {
            Icon(painter = painterResource(R.drawable.baseline_folder_open_24), contentDescription = "folder")
           // PillImage(imageUrl = imageRef) // Helpful image to assist in counting
        },
        trailingContent = {
            Text(text = quantity)
        },
        modifier = Modifier.height(75.dp)

    )
    Divider(color = Color.LightGray, modifier = Modifier.padding(start= 16.dp, end =16.dp)) // Divider to separate the records in the loop

}

// Preview function to preview composable elements without running full application
@Preview
@Composable
fun PreviewListItem() {
    FolderItem(title = "Inventory Day 1", quantity = "22")
}