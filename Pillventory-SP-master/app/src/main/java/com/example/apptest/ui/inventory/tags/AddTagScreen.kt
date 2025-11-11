package com.example.apptest.ui.inventory.tags

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.apptest.InventoryTopAppBar
import com.example.apptest.R
import com.example.apptest.ui.inventory.InventoryDestination
import com.example.apptest.ui.inventory.InventoryItem
import com.example.apptest.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

// Defines related information to the Add Tag page - route and title reference
object AddTagDestination : NavigationDestination {
    override val route = "addTag"
    override val titleRes = R.string.add_tag_title // Added to XML because this is type Int
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTagScreen(
    navController: NavHostController,
    viewModel: FetchRecTagViewModel = hiltViewModel() // Injecting the ViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItemIndex by rememberSaveable { mutableStateOf(1) } // Assuming "Add Tag" is the second item

    val items = listOf(
        InventoryItem(
            title = "Inventory View",
            selectedIcon = painterResource(R.drawable.baseline_photo_library_24),
            unselectedIcon = painterResource(R.drawable.outline_photo_library_24),
            navigationDestination = InventoryDestination
        ),
        InventoryItem(
            title = "Add Tag",
            selectedIcon = painterResource(R.drawable.baseline_photo_library_24), // Update for "Add Tag"
            unselectedIcon = painterResource(R.drawable.baseline_photo_library_24), // Update for "Add Tag"
            navigationDestination = AddTagDestination
        )
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                items.forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        label = { Text(text = item.title) },
                        selected = index == selectedItemIndex,
                        onClick = {
                            selectedItemIndex = index
                            scope.launch { drawerState.close() }
                            navController.navigate(item.navigationDestination.route)
                        },
                        icon = {
                            Icon(
                                painter = if (index == selectedItemIndex) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                InventoryTopAppBar(
                    title = stringResource(id = R.string.add_tag_title),
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
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                var state by remember { mutableStateOf(0) }
                val titles = listOf("Recommended", "Personal")

                // TabRow for switching between "Recommended" and "Personal"
                TabRow(selectedTabIndex = state) {
                    titles.forEachIndexed { index, title ->
                        Tab(
                            selected = state == index,
                            onClick = { state = index },
                            text = { Text(text = title) }
                        )
                    }
                }

                // Scrollable content area for the tags list
                Box(modifier = Modifier.weight(1f)) {
                    when (state) {
                        0 -> RecommendedTagsBody(viewModel) // Scrollable recommended tags list
                        1 -> PersonalTagsBody(viewModel) // Optionally another scrollable list
                    }
                }

                // Divider and "Save" button at the bottom
                Divider()
                Button(
                    onClick = {
                              viewModel.selectedTag.value?.let { viewModel.addTag(it) };
                            navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Save")
                }

            }
        }
    }
}

@Composable
fun RecommendedTagsBody(viewModel: FetchRecTagViewModel) {
    val tagsList = viewModel.tagsList // Assuming this is a List<String>
    var searchQuery by remember { mutableStateOf("") }
    val filteredTags = if (searchQuery.isBlank()) tagsList else tagsList.filter {
        it.contains(searchQuery, ignoreCase = true)
    }
    val selectedTag = viewModel.selectedTag.value

    Column {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Tags") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        LazyColumn {
            items(filteredTags) { tag ->
                TagListItem(
                    tag = tag,
                    isSelected = tag == selectedTag,
                    onTagClicked = { viewModel.toggleTagSelection(it) }
                )
            }
        }
    }
}


@Composable
fun TagsList(tags: List<String>, selectedTag: String?, onTagClicked: (String) -> Unit) {
    LazyColumn {
        items(tags) { tag ->
            TagListItem(
                tag = tag,
                isSelected = tag == selectedTag,
                onTagClicked = onTagClicked
            )
        }
    }
}


@Composable
fun TagListItem(tag: String, isSelected: Boolean, onTagClicked: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTagClicked(tag) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onTagClicked(tag) } // This now directly updates based on the radio button
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = tag)
    }
}

@Composable
fun PersonalTagsBody(viewModel: FetchRecTagViewModel) {
    // This assumes viewModel has a variable called personalTagsList for custom tags
    val personalTagsList = viewModel.customTagsList
    var searchQuery by remember { mutableStateOf("") }
    val filteredPersonalTags = if (searchQuery.isBlank()) personalTagsList else personalTagsList.filter {
        it.contains(searchQuery, ignoreCase = true)
    }
    val selectedTag = viewModel.selectedTag.value

    Column {
        // Search functionality for personal tags
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Personal Tags") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        // Display the list of personal (custom) tags
        TagsList(
            tags = filteredPersonalTags,
            selectedTag = selectedTag,
            onTagClicked = { tag -> viewModel.toggleTagSelection(tag) }
        )
    }
}
