package com.example.apptest.ui.inventory.alarm

import android.app.TimePickerDialog
import android.os.Handler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.apptest.InventoryTopAppBar
import com.example.apptest.R
import com.example.apptest.ui.inventory.InventoryDestination
import com.example.apptest.ui.inventory.tags.AddTagDestination
import com.example.apptest.ui.landing.tagmanagement.TagScreeDestination
import com.example.apptest.ui.inventory.calculator.CalculatorDestination
import com.example.apptest.ui.inventory.InventoryItem
import com.example.apptest.ui.inventory.folders.FolderDestination
import com.example.apptest.ui.inventory.metrics.MetricsScreenDestination
import com.example.apptest.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

object AlarmScreenDestination : NavigationDestination {
    override val route = "AlarmScreen"
    override val titleRes = R.string.set_alarm_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetAlarmScreen(
    navController: NavHostController,
    viewModel: AlarmViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var selectedItemIndex by rememberSaveable { mutableStateOf(4) }

    val items = listOf(
        InventoryItem( //0
            title = "Inventory View",
            selectedIcon = painterResource(R.drawable.baseline_photo_library_24),
            unselectedIcon = painterResource(R.drawable.outline_photo_library_24),
            navigationDestination = InventoryDestination
        ),
        InventoryItem(//1
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
        InventoryItem(//3
            title = "Calculator",
            selectedIcon = painterResource(R.drawable.baseline_calculate_24),
            unselectedIcon = painterResource(R.drawable.outline_calculate_24),
            navigationDestination = CalculatorDestination
        ),
//        InventoryItem(
//            title = "Add Tags",
//            selectedIcon = painterResource(R.drawable.baseline_bookmark_24),
//            unselectedIcon = painterResource(R.drawable.baseline_bookmark_border_24),
//            navigationDestination = AddTagDestination
//        ),
        InventoryItem(//4
            title = "Set Alarm",
            selectedIcon = painterResource(R.drawable.baseline_timer_24),
            unselectedIcon = painterResource(R.drawable.outline_timer_24),
            navigationDestination = AlarmScreenDestination
        ),
        InventoryItem(//5
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
                items.forEachIndexed { index, item ->
                    NavigationDrawerItem(
                        label = { Text(text = item.title) },
                        selected = index == selectedItemIndex,
                        onClick = {
                            selectedItemIndex = index
                            scope.launch { drawerState.close() }
                            navController.navigate(item.navigationDestination.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
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
                    title = stringResource(id = R.string.set_alarm_title),
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
                    .padding(16.dp)
            ) {
                Text("Set an Alarm", style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    val currentTime = Calendar.getInstance()
                    TimePickerDialog(
                        context,
                        { _, _, _ -> },
                        currentTime.get(Calendar.HOUR_OF_DAY),
                        currentTime.get(Calendar.MINUTE),
                        false // 24-hour view
                    ).show()

                    Handler().postDelayed({
                        val alarmTime = Calendar.getInstance()
                        alarmTime.add(Calendar.SECOND, 60)
                        viewModel.scheduleAlarm(alarmTime.timeInMillis)
                        // Show a confirmation message
                    }, 10000) // 10000 milliseconds = 10 seconds
                }) {
                    Text("Pick Time")
                }

            }
        }
    }
}
