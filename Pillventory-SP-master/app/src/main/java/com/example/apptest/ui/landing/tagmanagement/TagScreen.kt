package com.example.apptest.ui.landing.tagmanagement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.apptest.InventoryTopAppBar
import com.example.apptest.R
import com.example.apptest.ui.inventory.InventoryDestination
import com.example.apptest.ui.inventory.InventoryItem
import com.example.apptest.ui.inventory.alarm.AlarmScreenDestination
import com.example.apptest.ui.inventory.calculator.CalculatorDestination
import com.example.apptest.ui.inventory.folders.FolderDestination
import com.example.apptest.ui.inventory.metrics.MetricsScreenDestination
import com.example.apptest.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch


object TagScreeDestination: NavigationDestination {
    override val route = "TagScreen"
    override val titleRes = R.string.Tag_Screen
}

@Composable
fun TagItem(tag: String) {
    Text(text = tag, modifier = Modifier.padding(8.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagManagementScreen(
    navController: NavHostController,
    viewModel: UserTagsViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(1) }
    var newTagText by rememberSaveable { mutableStateOf("") }

    viewModel.fetchUserCustomTags()
    val tags = viewModel.customTagsList // This should be observed from ViewModel

    val items = listOf(
        InventoryItem( // 0
            title = "Inventory View",
            selectedIcon = painterResource(R.drawable.baseline_photo_library_24),
            unselectedIcon = painterResource(R.drawable.outline_photo_library_24),
            navigationDestination = InventoryDestination
        ),
        InventoryItem( //1
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
        InventoryItem(// 3
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
                                painter = if (index == selectedItemIndex) {
                                    inventoryItem.selectedIcon
                                } else inventoryItem.unselectedIcon,
                                contentDescription = inventoryItem.title
                            )
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
    ) {
        Scaffold (
            topBar = {
                InventoryTopAppBar(
                    title = stringResource(TagScreeDestination.titleRes),
                    menuClick = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.BottomCenter // Align content to bottom
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Divider(modifier = Modifier.fillMaxWidth().height(1.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f).fillMaxSize()
                    ) {
                        items(tags.size) { index ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Absolute.Center,
                                modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth()
                            ) {
                                TagItem(tag = tags[index])

                                Button(
                                    onClick = {
                                        // Remove tag logic
                                        viewModel.repository.getUID()?.let {
                                            viewModel.removeUserCustomTagUsingPillId(
                                                it, tags[index])
                                        }

                                    },
                                    modifier = Modifier.padding(start = 16.dp).width(100.dp).height(36.dp)
                                ) {
                                    Text("Remove")
                                }
                            }
                        }
                    }

                    Divider(modifier = Modifier.fillMaxWidth().height(1.dp))

                    TextField(
                        value = newTagText,
                        onValueChange = { newTagText = it },
                        label = { Text("New Tag") },
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    )

                    Button(
                        onClick = {
                            // Add tag logic
                            viewModel.repository.getUID()
                                ?.let { viewModel.addUserCustomTagUsingPillId(it, newTagText) }
                            newTagText = "" // Clear the text field after adding
                        },
                        modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp).height(48.dp).fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text("Add Tag")
                    }
                }
            }
        }
    }
}




//@Preview
//@Composable
//fun TagManagementScreenPreview() {
//    // Create a NavHostController instance for preview
//    val navController = rememberNavController()
//
//    // Call the TagManagementScreen composable with some sample parameters
//    TagManagementScreen(navController = navController)
//}