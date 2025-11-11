package com.example.apptest.ui.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.apptest.BottomNavigationBar
import com.example.apptest.InventoryTopAppBar
import com.example.apptest.R
import com.example.apptest.ui.inventory.alarm.AlarmScreenDestination
import com.example.apptest.ui.inventory.metrics.MetricsScreenDestination
import com.example.apptest.ui.inventory.calculator.CalculatorDestination
import com.example.apptest.ui.inventory.tags.AddTagDestination
import com.example.apptest.ui.landing.tagmanagement.TagScreeDestination
import com.example.apptest.ui.inventory.cards.RecordCard
import com.example.apptest.ui.inventory.cards.VerticalGridCard
import com.example.apptest.ui.inventory.folders.FolderDestination
import com.example.apptest.ui.landing.guest.GuestRerouteScreen
import com.example.apptest.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

// Defines related information to the Inventory page - route and title reference
object InventoryDestination: NavigationDestination {
    override val route = "inventory"
    override val titleRes = R.string.inventory_title // Added to XML because this is type Int
}

// Defines Inventory Menu Items
data class InventoryItem( // Define routes later
    val title: String,
    val selectedIcon: Painter,
    val unselectedIcon: Painter,
    //val navigationFunction: () -> Unit
    val navigationDestination: NavigationDestination
    //val route: String
)

// Inventory Screen composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
  //  navigateTagManagement: ()-> Unit,
   // navigateSearchFilter: ()-> Unit,
    navigateLogin: ()-> Unit,
    navigateSignIn: () -> Unit,
    navigateRecord: (String) -> Unit,
   // navigateCalculator: () -> Unit,
    navController: NavHostController
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItemIndex by rememberSaveable {
        mutableStateOf(0)
    }

    val items = listOf(
        // Will define routes later (when pages are created) since theres no references to routes now
        InventoryItem( // 0
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
        InventoryItem( // 2
            title = "Folders",
            selectedIcon = painterResource(R.drawable.baseline_folder_24),
            unselectedIcon = painterResource(R.drawable.baseline_folder_open_24),
            navigationDestination = FolderDestination
        ),
        InventoryItem( // 3
            title = "Calculator",
            selectedIcon = painterResource(R.drawable.baseline_calculate_24),
            unselectedIcon = painterResource(R.drawable.outline_calculate_24),
            navigationDestination = CalculatorDestination
        ),
//        InventoryItem(
//            title = "Add Tags",
//            selectedIcon = painterResource(R.drawable.baseline_alarm_24),
//            unselectedIcon = painterResource(R.drawable.baseline_alarm_24),
//            navigationDestination = AddTagDestination
//        ),
        InventoryItem( //4
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

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet { Spacer(modifier = Modifier.height(16.dp))

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
                                painter = if (index == selectedItemIndex) {
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
                    // Top bar will be adjusted for Tag Management Button, Calculator, and Files/Folders
                    title = stringResource(InventoryDestination.titleRes),
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
            bottomBar = {
                BottomNavigationBar(navController)
            }
        ) { innerPadding ->
            InventoryBody(
                modifier = Modifier.padding(innerPadding),
                navigateLogin = navigateLogin,

                navigateSignIn = navigateSignIn,
                navigateRecord = navigateRecord
            )
        }
    }
}

// Function related to generating the body of Inventory
@Composable
private fun InventoryBody(
    modifier : Modifier,
    navigateLogin: () -> Unit,
    navigateSignIn: () -> Unit,
    navigateRecord: (String) -> Unit,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    Column (
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp) // Adjust later (?)
    ) {

        /**
         * Need to distinguish between account users and guest users
         */
        val isGuest = viewModel.isUserGuest()

        /**
         * Variables in UI to track if Card or Grid view for verified users
         */
        val isCardView = viewModel.isCardLayout.collectAsState()
        val isGridView = viewModel.isGridLayout.collectAsState()


        /**
         * Main body composable:
         */

        if(isGuest) { // Bar guest user from viewing the Inventory page until they create an account
            GuestRerouteScreen(
                navigateLogin = navigateLogin,
                navigateSignIn = navigateSignIn
            )
        } else { // User has a verified user account
            Column {

                // Representing the tool bar for functions like filtering, viewing, and extra
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Navigate to search/filter page - TO BE IMPLEMENTED

                    DropdownMenuForFilter(
                        filterByDateAscending = {  },
                        filterByDateDescending = { /*TODO*/ },
                        filterByTagAscending = { /*TODO*/ },
                        filterByTagDescending = { /*TODO*/ }) {

                    }

                    Spacer(modifier = Modifier.weight(1f)) // gap between composables

                    // Segmented buttons not available for Jetpack Compose yet so using Outline Card with Divider for same effect

                    OutlinedCard (modifier = Modifier.height(45.dp)) {
                        Row {
                            // Changes view to Card List
                            IconButton(onClick = { viewModel.changeLayoutToCard() }) {
                                Icon(painter = painterResource(R.drawable.baseline_view_list_24), contentDescription = "View list")
                            }

                            // Visually separate both options
                            Divider(
                                color = Color.DarkGray,
                                modifier = Modifier
                                    .fillMaxHeight()  // fill the max height of container
                                    .width(1.dp)
                            )

                            // Changes view to Grid List
                            IconButton(onClick = { viewModel.changeLayoutToGrid() }) {
                                Icon(painter = painterResource(R.drawable.baseline_view_module_24), contentDescription = "View gallery")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Retrieve list of pills from database - default to view list mode
                if(isCardView.value) {
                    PillList(navigateRecord)
                } else if (isGridView.value) {
                    PillGrid(navigateRecord)
                }
            }
        }
    }
}


/**
 *  Composable function for Pill Card List
 */
@Composable
fun PillList( // For listed card objects
    navigateRecord: (String) -> Unit,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel) {
        viewModel.fetchPillIds()
    }

    Column {
        LazyColumn {
            items(viewModel.pillRecords) { record ->
                //Text("Pill ID: ${record.pillId}") // Check for individual unique record
                RecordCard(
                    record.pillId,
                    record.count,
                    record.date,
                    record.description,
                    record.tags,
                    record.imageRef,
                    navigateRecord
                )
            }
        }
    }
}

/**
 *  Composable function for Pill Grid Lists
 */
@Composable
fun PillGrid( // For grid objects
    navigateRecord: (String) -> Unit,
    viewModel: InventoryViewModel= hiltViewModel()
) {
    LaunchedEffect(viewModel) {
        viewModel.fetchPillIds()
    }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(128.dp),
        content = {
            items(viewModel.pillRecords) { record ->
                VerticalGridCard(
                    record.pillId,
                    record.count,
                    record.description,
                    record.date,
                    record.tags,
                    record.imageRef,
                    navigateRecord
                )
            }
        }
    )
}

@Composable
//fun DropdownMenuForFilter(
//    filterByDateAscending: () -> Unit,
//    filterByDateDescending: () -> Unit,
//    filterByTagAscending: () -> Unit,
//    filterByTagDescending: () -> Unit
//) {
//    val expanded = remember { mutableStateOf(false) }
//
//    Box {
//        Button(
//            onClick = { expanded.value = true },
//            shape = RoundedCornerShape(10.dp)
//        ) {
//            Icon(painter = painterResource(R.drawable.baseline_filter_alt_24), contentDescription = "Navigate to search and filter items")
//            Text(text = "Filter")
//        }
//    }
//
//        DropdownMenu(
//            expanded = expanded.value,
//            onDismissRequest = { expanded.value = false },
//            modifier = Modifier.width(200.dp)
//        ) {
//            DropdownMenuItem(
//                leadingIcon = { androidx.compose.material.Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Filter Date Ascending option" ) },
//                text = { androidx.compose.material.Text(text = "Date Ascending") },
//                onClick = { filterByDateAscending(); expanded.value = false }
//            )
//            DropdownMenuItem(
//                leadingIcon = { androidx.compose.material.Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Filter Date Descending option" ) },
//                text = { androidx.compose.material.Text(text = "Date Descending") },
//                onClick = { filterByDateDescending(); expanded.value = false }
//            )
//            DropdownMenuItem(
//                leadingIcon = { androidx.compose.material.Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Filter Tag Ascending option" ) },
//                text = { androidx.compose.material.Text(text = "Tag Ascending") },
//                onClick = { filterByTagAscending(); expanded.value = false }
//            )
//            DropdownMenuItem(
//                leadingIcon = { androidx.compose.material.Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "Filter Teg Descending option" ) },
//                text = { androidx.compose.material.Text(text = "Tag Descending") },
//                onClick = { filterByTagDescending(); expanded.value = false }
//            )
//        }
//    }
fun DropdownMenuForFilter(
    filterByDateAscending: () -> Unit,
    filterByDateDescending: () -> Unit,
    filterByTagAscending: () -> Unit,
    filterByTagDescending: () -> Unit,
    filterByType: (String) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }
    val subMenuExpanded = remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("") }

    val viewModel: InventoryViewModel = hiltViewModel()

    Box(
       // modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopStart
    ) {
        Button(
            onClick = { expanded.value = true },
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(painter = painterResource(R.drawable.baseline_filter_alt_24), contentDescription = "Navigate to search and filter items")
            Text(text = "Filter")
        }

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier.width(200.dp).background(Color.White, RoundedCornerShape(4.dp))
        ) {
            DropdownMenuItem(
                leadingIcon = { Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Date Ascending") },
                text = { Text("Date Ascending") },
                onClick = { filterByDateAscending(); expanded.value = false;
                    viewModel.sortRecordsByDateAscending()
                }
            )
            DropdownMenuItem(
                leadingIcon = { Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Date Descending") },
                text = { Text("Date Descending") },
                onClick = { filterByDateDescending(); expanded.value = false;
                    viewModel.sortRecordsByDateDescending()
                }
            )
            DropdownMenuItem(
                leadingIcon = { Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "Count Ascending") },
                text = { Text("Count Ascending") },
                onClick = { filterByTagAscending(); expanded.value = false;
                    viewModel.sortRecordsByCountAscending()
                }
            )
            DropdownMenuItem(
                leadingIcon = { Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "Count Descending") },
                text = { Text("Count Descending") },
                onClick = { filterByTagDescending(); expanded.value = false;
                    //viewModel.fetchPillIds()
                    viewModel.sortRecordsByCountDescending()
                }
            )
            DropdownMenuItem(
                leadingIcon = { Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "More Filters") },
                text = { Text("More Filters") },
                onClick = { subMenuExpanded.value = true;
                    viewModel.fetchTags()
                }
            )
        }

        if (subMenuExpanded.value) {
            Popup(
                alignment = Alignment.TopEnd,
                onDismissRequest = { subMenuExpanded.value = false },
                properties = PopupProperties(
                    focusable = true,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .background(Color.White, RoundedCornerShape(4.dp))
                        .padding(4.dp)
                ) {
                    LazyColumn(modifier = Modifier.padding(top = 11.dp, bottom = 11.dp)) {
                        items(viewModel.tagsList) { tag ->
                            TagListItem(
                                tag = tag,
                                isSelected = viewModel.selectedTags.value.contains(tag),
                                onTagClicked = { viewModel.toggleTagSelection(tag);
                                        viewModel.sortRecordsByTag(tag)

                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TagListItem(tag: String, isSelected: Boolean, onTagClicked: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTagClicked(tag) } // Direct call to onTagClicked with tag
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onTagClicked(tag) } // onTagClicked when RadioButton state changes
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = tag)
    }
}



// Add possible improvements for further cases
// potential for improvement even though it isn't implemented
