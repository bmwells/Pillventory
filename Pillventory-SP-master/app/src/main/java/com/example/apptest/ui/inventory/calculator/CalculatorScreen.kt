package com.example.apptest.ui.inventory.calculator

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.apptest.BottomNavigationBar
import com.example.apptest.InventoryTopAppBar
import com.example.apptest.R
import com.example.apptest.ui.inventory.InventoryDestination
import com.example.apptest.ui.inventory.InventoryItem
import com.example.apptest.ui.inventory.alarm.AlarmScreenDestination
import com.example.apptest.ui.inventory.cards.PillImage
import com.example.apptest.ui.inventory.folders.FolderDestination
import com.example.apptest.ui.inventory.metrics.MetricsScreenDestination
import com.example.apptest.ui.inventory.tags.AddTagDestination
import com.example.apptest.ui.landing.tagmanagement.TagScreeDestination
import com.example.apptest.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch



// Defines related information to the Calculator page - route and title reference
object CalculatorDestination: NavigationDestination {
    override val route = "calculator"
    override val titleRes = R.string.calculator_title // Added to XML because this is type Int
}

// Calculator overall Composable
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    navController: NavHostController
) {

    /**
     * Variables
     */
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItemIndex by rememberSaveable {
        mutableStateOf(3) // The selected index has to be the index of the inventoryItem for this screen for the highlight to line up correctly. If zero it will default to the inventory home view being highlighted
    }

    // Define list of items in the side menu (Calculator for relevancy)
    val items = listOf(
        // Will define routes later (when pages are created) since theres no references to routes now

        InventoryItem( //0
            title = "Inventory View",
            selectedIcon = painterResource(R.drawable.baseline_photo_library_24),
            unselectedIcon = painterResource(R.drawable.outline_photo_library_24),
            navigationDestination = InventoryDestination
        ),
        InventoryItem(// 1
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
                    title = stringResource(CalculatorDestination.titleRes),
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
        ) { innerPadding ->

            // Define variables relevant to the calculator Tab operations
            var state by remember { mutableStateOf(0) }
            val titles = listOf("Add", "Multiply", "Subtract", "Divide")

            Column (modifier = Modifier.padding(innerPadding)) {
                TabRow(selectedTabIndex = state) {
                    titles.forEachIndexed { index, title ->
                        Tab (
                            selected = state == index,
                            onClick = { state = index },
                            text = {Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis)}
                        )

                    }
                }

                // Change operation state based on user interaction
                if(state == 0) {
                    CalculatorAddBody(Modifier.padding(innerPadding))
                } else if (state == 1) {
                    CalculatorMultBody()
                } else if (state == 2) {
                    CalculatorSubtractBody(Modifier.padding(innerPadding))
                } else if (state == 3) {
                    CalculatorDivBody()
                }
            }
        }
    }
}


// Defined body for add operation composable
@Composable
fun CalculatorAddBody(modifier: Modifier) {
    Box {
        CalculateListBoxes()
    }
}

// Defined body for subtraction operation composable
@Composable
fun CalculatorSubtractBody(modifier: Modifier) {
    Box {
        CalculateListBoxesSubtract()
    }
}

// Defined body for multiplication operation composable
@Composable
fun CalculatorMultBody() {
    Box {
        CalculateListCircles()
    }
}

// Defined body for division operation composable
@Composable
fun CalculatorDivBody() {
    Box {
        CalculateListCirclesDivision()
    }
}

// Subtraction composable for list boxes
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun CalculateListBoxesSubtract(
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel) {
        viewModel.fetchShortenedPills() // always fetch the pills when changing
    }
    Box {
        Column {
            LazyColumn {
                items(viewModel.shortenedPillRecords) {record ->
                    CalcListItem(
                        record.pillId,
                        record.count,
                        record.date,
                        record.imageRef,
                        record ,
                        onCheckedChange = {
                            viewModel.toggleItemSelectionForSubtr(record.pillId) // differentiate the check box with change function in view model
                        }
                    )
                }
            }
        }
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Column (modifier = Modifier.padding(top=7.dp, bottom=7.dp, start = 10.dp, end = 10.dp)) {
                Text(text = "Difference", fontSize = 16.sp, color = Color.DarkGray)
                // "Subtract From" - "Running Total" = "Difference"
                Text(text = viewModel.subtractFrom.value.toString() + " - " + viewModel.runningTotalForSubtr.value.toString() + " = " +viewModel.getDifference() , fontSize = 25.sp)
            }
        }


        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .align(Alignment.BottomCenter)
                .padding(10.dp)
        ) {
            // Text field for the user to define the total of the inventory they are subtracting from
            OutlinedTextField(
                value = viewModel.subtractFrom.collectAsState().value.toString(),

                onValueChange = { viewModel.onSubtractFromChange(it)
                    viewModel.getProduct(viewModel.upID.value)}, // Update value seen as user types it
                label = { Text(text = "Enter Inventory Total")}, // Add text to strings.xml
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// General calculate list boxes composable - used for addition operation
@Composable
fun CalculateListBoxes(
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel) {
        viewModel.fetchShortenedPills()
    }
    Box {
        Column {
            LazyColumn {
                items(viewModel.shortenedPillRecords) {record ->
                    CalcListItem(
                        record.pillId,
                        record.count,
                        record.date,
                        record.imageRef,
                        record ,
                        onCheckedChange = {
                            viewModel.toggleItemSelection(record.pillId)
                        }
                    )
                }
            }
        }
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Column (modifier = Modifier.padding(top=7.dp, bottom=7.dp, start = 10.dp, end = 10.dp)) {
                Text(text = "Running Total", fontSize = 16.sp, color = Color.DarkGray)
                Text(text = viewModel.runningTotal.value.toString(), fontSize = 25.sp,)
            }
        }
    }
}

// Specific list item and details for count, date, check box
@Composable
fun CalcListItem(
    pillId: String,
    pillCount: String,
    date: String,
    imageRef: String,
    record: ShortenedPillRecord,
    onCheckedChange: (Boolean) -> Unit
) {

    val checkedState = remember { mutableStateOf(false) }

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

                    checkedState.value = it // When the check box is checked/unchecked change its state
                    onCheckedChange(it)
                }
            )
        },
        modifier = Modifier.height(65.dp)
        
    )
    Divider(color = Color.LightGray, modifier = Modifier.padding(start= 16.dp, end =16.dp)) // Divider to separate the records in the loop

}



// Composable for division operation
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun CalculateListCirclesDivision(
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel) {
        viewModel.fetchShortenedPills()
    }
    Box (modifier = Modifier.fillMaxSize()) {
        Column {
            LazyColumn {
                items(viewModel.shortenedPillRecords) {record ->
                    CalcListRadioItem(
                        record.pillId,
                        record.count,
                        record.date,
                        record.imageRef,
                        record ,
                        onSelectedChange = {
                            viewModel.getQuotient(record.pillId) // Function to differentiate with division for finding quotient
                        }
                    )
                }
            }
        }
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Column (modifier = Modifier.padding(top=7.dp, bottom=7.dp, start = 10.dp, end = 10.dp)) {
                Text(text = "Quotient", fontSize = 16.sp, color = Color.DarkGray)
                Text(text = viewModel.quotient.value.toString(), fontSize = 25.sp,) // Display the calculated quotient value
            }
        }

        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .align(Alignment.BottomCenter)
                .padding(10.dp)
        ) {
            // Adding a text field to get user input for the division factor - note it can NOT be 0
            OutlinedTextField(
                value = viewModel.divFactor.collectAsState().value.toString(),

                onValueChange = { viewModel.onDivChange(it)
                    viewModel.getQuotient(viewModel.upID.value)}, // Update value seen as user types it
                label = { Text(text = "Enter Division Factor")}, // Add text to strings.xml
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


// List composable for radio buttons instead of check boxes - used for the product operation
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun CalculateListCircles(
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    LaunchedEffect(viewModel) {
        viewModel.fetchShortenedPills()
    }
    Box (modifier = Modifier.fillMaxSize()) {
        Column {
            LazyColumn {
                items(viewModel.shortenedPillRecords) {record ->
                    CalcListRadioItem(
                        record.pillId,
                        record.count,
                        record.date,
                        record.imageRef,
                        record ,
                        onSelectedChange = {
                            viewModel.getProduct(record.pillId) // function to differentiate the multiplcation operation
                        }
                    )
                }
            }
        }
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Column (modifier = Modifier.padding(top=7.dp, bottom=7.dp, start = 10.dp, end = 10.dp)) {
                Text(text = "Product", fontSize = 16.sp, color = Color.DarkGray)
                Text(text = viewModel.product.value.toString(), fontSize = 25.sp,) // Display the calculated product
            }
        }

        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .align(Alignment.BottomCenter)
                .padding(10.dp)
        ) {
            // Inputted text field for user to input the multiplication function
            OutlinedTextField(
                value = viewModel.multFactor.collectAsState().value.toString(),

                onValueChange = { viewModel.onMultFactorChange(it)
                    viewModel.getProduct(viewModel.upID.value)}, // Update value seen as user types it
                label = { Text(text = "Enter Multiplication Factor")}, // Add text to strings.xml
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Composable
fun CalcListRadioItem(
    pillId: String,
    pillCount: String,
    date: String,
    imageRef: String,
    record: ShortenedPillRecord,
    onSelectedChange: () -> Unit,
    viewModel: CalculatorViewModel = hiltViewModel()
) {

    val selectedState = remember { mutableStateOf(false) }

    ListItem(
        headlineContent = {
            Text(text = "Pill Count: $pillCount") // Displays the pill count
        },
        supportingContent = {
            Text(text = date) // Displays the date
        },
        leadingContent = {
            PillImage(imageUrl = imageRef) // Related image
        },
        trailingContent = {
            RadioButton( // Displays a radio button instead of a checkbox for more singular selection option (not multi select)
                selected = selectedState.value,
                onClick = { // background calculation

                    selectedState.value = !selectedState.value
                    onSelectedChange()
                    viewModel.setID(pillId)
                }
            )
        },
        modifier = Modifier.height(65.dp)
    )
    Divider(color = Color.LightGray, modifier = Modifier.padding(start= 16.dp, end =16.dp))

}
