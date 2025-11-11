package com.example.apptest


import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.DrawerState
import androidx.compose.material.ModalDrawer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.apptest.ui.camera.CameraDestination
import com.example.apptest.ui.inventory.InventoryDestination
import com.example.apptest.ui.settings.SettingsDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Top level composable that represents screens for the application.
 */
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun App(navController: NavHostController = rememberNavController()) {
//    PillNavHost(navController = navController)
//}

/**
 * App bar to display title and conditionally display the back navigation.
 */
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AppTestTopAppBar(
//    title: String,
//    canNavigateBack: Boolean,
//    modifier: Modifier = Modifier,
//    scrollBehavior: TopAppBarScrollBehavior? = null,
//    navigateUp: () -> Unit = {}
//) {
//    CenterAlignedTopAppBar(
//        title = { Text(title) },
//        modifier = modifier,
//        scrollBehavior = scrollBehavior,
//        navigationIcon = {
//            if (canNavigateBack) {
//                IconButton(onClick = navigateUp) {
//                    Icon(
//                        imageVector = Filled.ArrowBack,
//                        contentDescription = stringResource(string.back_button)
//                    )
//                }
//            }
//        }
//    )
//}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PillTopAppBar(
    title: String,
    canNavigateBack: Boolean, // False for navigation pages
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if(canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back button icon" // Place in strings.xml later
                    )
                }
            }
        }
    )
}

// Unique inventory top bar for side menu expansion
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    menuClick: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = menuClick
            ) {
                Icon( // Menu button
                    painter = painterResource(R.drawable.baseline_menu_24),
                    contentDescription = "menu button icon"
                )
            }
        }
    )
}


// Bottom Navigation more simplified
data class Item(
    val route: String,
    @DrawableRes val icon: Int
)
@Composable
fun BottomNavigationBar(navController: NavController) {
    val itemList = listOf(
        Item(InventoryDestination.route, R.drawable.baseline_photo_library_24),
        Item(CameraDestination.route, R.drawable.baseline_camera_alt_24),
        Item(SettingsDestination.route, R.drawable.baseline_settings_24)
    )

    BottomNavigation(
        backgroundColor = Color.LightGray
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        itemList.forEach { item ->
            val isSelected = currentRoute == item.route

            BottomNavigationItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = "") },
                selectedContentColor = colorResource(R.color.purple_m),
                unselectedContentColor = Color.Gray,
                alwaysShowLabel = false,
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { screenRoute ->
                            popUpTo(screenRoute) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}