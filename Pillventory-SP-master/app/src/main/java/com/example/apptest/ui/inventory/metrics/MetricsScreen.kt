package com.example.apptest.ui.inventory.metrics

import androidx.compose.ui.graphics.Color
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
import com.example.apptest.ui.inventory.alarm.AlarmScreenDestination
import com.example.apptest.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch
import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.example.apptest.ui.inventory.folders.FolderDestination

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2


object MetricsScreenDestination : NavigationDestination {
    override val route = "Metrics Screen"
    override val titleRes = R.string.metrics_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetricsScreen(
    navController: NavHostController,
    viewModel: MetricsViewModel = hiltViewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var selectedItemIndex by rememberSaveable { mutableStateOf(5) }

    val items = listOf(
        InventoryItem(
            title = "Inventory View",
            selectedIcon = painterResource(R.drawable.baseline_photo_library_24),
            unselectedIcon = painterResource(R.drawable.outline_photo_library_24),
            navigationDestination = InventoryDestination
        ),
        InventoryItem(
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
        InventoryItem(
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
        InventoryItem(
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
                    title = stringResource(id = R.string.metrics_title),
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
            MetricsBody(
                modifier = Modifier.padding(innerPadding),
                navController = navController
                //navigateLogin = navigateLogin,
                //navigateSignIn = navigateSignIn,
            )
        }
    }
}

@Composable
fun MetricsBody(
    modifier: Modifier = Modifier,
    //navigateLogin: () -> Unit,
    //navigateSignIn: () -> Unit,
    viewModel: MetricsViewModel = hiltViewModel(),
    navController:NavHostController
) {
    Column (
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp) // Adjust later (?)
    ) {
        /**
         * Need to distinguish between account users and guest users
         */
        val isGuest = viewModel.isUserGuest()

        /**
         * Main body composable:
         */
        if (isGuest) { // Bar guest user from viewing the Inventory page until they create an account
            //GuestRerouteScreen(
                //navigateLogin = navigateLogin,
                //navigateSignIn = navigateSignIn
            //)
            navController.popBackStack()
        } else { // User has a verified user account

            LaunchedEffect(viewModel) {
                viewModel.fetchPillIds()
            }

            val graphList = createPieList(viewModel, colorList)
            val firstRow: PieChartInput? = graphList.firstOrNull()
            val rowCount = graphList.size


            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                // Metrics
                //Pie Chart
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp) // Add padding to the sides
                        .height(400.dp), // Adjust the height as needed
                    contentAlignment = Alignment.Center // Center the pie chart horizontally
                ) {
                    // Pie Chart
                    if (firstRow != null) {
                        val pieChartInputs = graphList.map { row ->
                            PieChartInput(
                                color = row.color,
                                value = row.value,
                                description = row.description
                            )
                        }
                        PieChart(
                            modifier = Modifier
                                .size(200.dp),
                            input = pieChartInputs,



                            centerText = "Top Tags in Inventory"
                        )
                    }

                }

                Spacer(modifier = Modifier.height(64.dp))

                // For Tag List
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp) // Add padding to the sides and top/bottom
                        .offset(x = 0.dp, y = (-64).dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray) // Background color for visibility
                            .padding(8.dp) // Add padding to the content inside the box
                    ) {
                        // Rows of tags and counts
                        TagList()
                    }
                }
            }
        }
    }
}


// Creates List to use for row list
fun createList(viewModel: MetricsViewModel): List<Triple<String, Int, Int>> {
    val tagCounts = mutableMapOf<String, Int>()
    viewModel.fetchPillIds() // Fetch pill records from ViewModel

    viewModel.pillRecords.forEach { record ->
        val tags = record.tags.split(",").map { it.trim() }
        for (tag in tags) {
            val count = record.count.toInt()
            tagCounts[tag] = tagCounts.getOrDefault(tag, 0) + count
        }
    }

    val totalCount = tagCounts.values.sum()
    // Calculate the percentage for each tag and create a list with tag, count, and percentage
    return tagCounts.toList()
        .sortedByDescending { it.second }
        .map { (tag, count) ->
            val percentage = ((count.toDouble() / totalCount) * 100).toInt()
            Triple(tag, count, percentage)
        }
}

fun createPieList(viewModel: MetricsViewModel, colorList: List<Color>): List<PieChartInput> {
    val tagCounts = mutableMapOf<String, Int>()
    viewModel.fetchPillIds() // Fetch pill records from ViewModel
    var totalCount = 0 // Total count of all tags

    // Count tags and calculate total count
    viewModel.pillRecords.forEach { record ->
        val tags = record.tags.split(",").map { it.trim() }
        for (tag in tags) {
            val count = record.count.toInt()
            tagCounts[tag] = tagCounts.getOrDefault(tag, 0) + count
            totalCount += count
        }
    }

    // Sort the tagPercentages map by value (percentage) in descending order
    val sortedList = tagCounts.toList().sortedByDescending { it.second }

    // Create list of PieChartInput objects
    return sortedList.mapIndexed { index, (tag, count) ->
        val colorIndex = index % colorList.size // Use modulo to cycle through colorList
        PieChartInput(colorList[colorIndex], count, tag, false)
    }
}

// Composable for the rows of tags and their counts and percentages
@Composable
fun TagList(
    viewModel: MetricsViewModel = hiltViewModel()
) {
    val tagList = createList(viewModel)

    LazyColumn {
        items(tagList) { (tag, totalCount, percent) ->
            RecordRow(tag, totalCount.toString(), percent.toString())
        }
    }
}

@Composable
fun RecordRow(
    tag: String,
    totalCount: String,
    percent: String,
    viewModel: MetricsViewModel = hiltViewModel()
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = tag, fontWeight = FontWeight.Bold)
        Text(text = "Total Count: $totalCount")
        Text(text = "$percent% of Inventory")
    }
}


// Pie Chart
@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    radius:Float = 400f,
    innerRadius:Float = 250f,
    transparentWidth:Float = 70f,
    input:List<PieChartInput>,
    centerText:String = ""
) {
    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }


    var inputList by remember {
        mutableStateOf(input)
    }
    var isCenterTapped by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ){
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(true) {
                    detectTapGestures(
                        onTap = { offset ->
                            val tapAngleInDegrees = (-atan2(
                                x = circleCenter.y - offset.y,
                                y = circleCenter.x - offset.x
                            ) * (180f / PI).toFloat() - 90f).mod(360f)
                            val centerClicked = if (tapAngleInDegrees < 90) {
                                offset.x < circleCenter.x + innerRadius && offset.y < circleCenter.y + innerRadius
                            } else if (tapAngleInDegrees < 180) {
                                offset.x > circleCenter.x - innerRadius && offset.y < circleCenter.y + innerRadius
                            } else if (tapAngleInDegrees < 270) {
                                offset.x > circleCenter.x - innerRadius && offset.y > circleCenter.y - innerRadius
                            } else {
                                offset.x < circleCenter.x + innerRadius && offset.y > circleCenter.y - innerRadius
                            }

                            if (centerClicked) {
                                inputList = inputList.map {
                                    it.copy(isTapped = !isCenterTapped)
                                }
                                isCenterTapped = !isCenterTapped
                            } else {
                                val anglePerValue = 360f / input.sumOf {
                                    it.value
                                }
                                var currAngle = 0f
                                inputList.forEach { pieChartInput ->

                                    currAngle += pieChartInput.value * anglePerValue
                                    if (tapAngleInDegrees < currAngle) {
                                        val description = pieChartInput.description
                                        inputList = inputList.map {
                                            if (description == it.description) {
                                                it.copy(isTapped = !it.isTapped)
                                            } else {
                                                it.copy(isTapped = false)
                                            }
                                        }
                                        return@detectTapGestures
                                    }
                                }
                            }
                        }
                    )
                }
        ){
            val width = size.width
            val height = size.height
            circleCenter = Offset(x= width/2f,y= height/2f)

            val totalValue = input.sumOf {
                it.value
            }
            val anglePerValue = 360f/totalValue
            var currentStartAngle = 0f

            inputList.forEach { pieChartInput ->
                val scale = if(pieChartInput.isTapped) 1.1f else 1.0f
                val angleToDraw = pieChartInput.value * anglePerValue
                scale(scale){
                    drawArc(
                        color = pieChartInput.color,
                        startAngle = currentStartAngle,
                        sweepAngle = angleToDraw,
                        useCenter = true,
                        size = Size(
                            width = radius*2f,
                            height = radius*2f
                        ),
                        topLeft = Offset(
                            (width-radius*2f)/2f,
                            (height - radius*2f)/2f
                        )
                    )
                    currentStartAngle += angleToDraw
                }
                var rotateAngle = currentStartAngle-angleToDraw/2f-90f
                var factor = 1f
                if(rotateAngle>90f){
                    rotateAngle = (rotateAngle+180).mod(360f)
                    factor = -0.92f
                }

                val percentage = (pieChartInput.value/totalValue.toFloat()*100).toInt()

                drawContext.canvas.nativeCanvas.apply {
                    if(percentage>3){
                        rotate(rotateAngle){
                            drawText(
                                "$percentage %",
                                circleCenter.x,
                                circleCenter.y+(radius-(radius-innerRadius)/2f)*factor,
                                Paint().apply {
                                    textSize = 20.sp.toPx()
                                    textAlign = Paint.Align.CENTER
                                    color = darkBlue.toArgb()
                                }
                            )
                        }
                    }
                }
                if(pieChartInput.isTapped){
                    val tabRotation = currentStartAngle - angleToDraw - 90f
                    rotate(tabRotation){
                        drawRoundRect(
                            topLeft = circleCenter,
                            size = Size(12f,radius*1.2f),
                            color = gray,
                            cornerRadius = CornerRadius(15f,15f)
                        )
                    }
                    rotate(tabRotation+angleToDraw){
                        drawRoundRect(
                            topLeft = circleCenter,
                            size = Size(12f,radius*1.2f),
                            color = gray,
                            cornerRadius = CornerRadius(15f,15f)
                        )
                    }
                    rotate(rotateAngle){
                        drawContext.canvas.nativeCanvas.apply {
                            drawText(
                                "${pieChartInput.description}: ${pieChartInput.value}",
                                circleCenter.x,
                                circleCenter.y + radius*1.3f*factor,
                                Paint().apply {
                                    textSize = 18.sp.toPx()
                                    textAlign = Paint.Align.CENTER
                                    color = darkBlue.toArgb()
                                    isFakeBoldText = true
                                }
                            )
                        }
                    }
                }
            }

            if(inputList.first().isTapped){
                rotate(-90f){
                    drawRoundRect(
                        topLeft = circleCenter,
                        size = Size(12f,radius*1.2f),
                        color = gray,
                        cornerRadius = CornerRadius(15f,15f)
                    )
                }
            }
            drawContext.canvas.nativeCanvas.apply {
                drawCircle(
                    circleCenter.x,
                    circleCenter.y,
                    innerRadius,
                    Paint().apply {
                        color = white.copy(alpha = 0.6f).toArgb()
                        setShadowLayer(10f,0f,0f, white.toArgb())
                    }
                )
            }

            drawCircle(
                color = white.copy(0.2f),
                radius = innerRadius+transparentWidth/2f
            )

        }
        Text(
            centerText,
            modifier = Modifier
                .width(Dp(innerRadius / 1.5f))
                .padding(25.dp),
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            textAlign = TextAlign.Center
        )

    }
}

data class PieChartInput(
    val color:Color,
    val value:Int,
    val description:String,
    val isTapped:Boolean = false
)

// Pie Chart Colors
val darkBlue = Color(0xFF18192b)
val white = Color(0xFFF3F3F3)
val gray = Color(0xFF3F3F3F)
val colorList = listOf(
        Color(0xFFBB86FC), // Purple200
        Color(0xFF03DAC5), // Teal200
        Color(0xFFe84a23), // redOrange
        Color(0xFF0ddb25), // green
        Color(0xFF027cf5), // brightBlue
        Color(0xFFFF7F50), // Coral
        Color(0xFF00FFFF), // Cyan
        Color(0xFFFFD700), // Gold
        Color(0xFFBA55D3), // MediumOrchid
        Color(0xFF4169E1), // RoyalBlue
        Color(0xFF32CD32), // LimeGreen
        Color(0xFFFF6347), // Tomato
        Color(0xFF40E0D0), // Turquoise
        Color(0xFFFF4500), // OrangeRed
        Color(0xFF8A2BE2), // BlueViolet
        Color(0xFF00FF7F),  // SpringGreen
        Color(0xFFFF1493), // DeepPink
        Color(0xFF00BFFF), // DeepSkyBlue
        Color(0xFF9370DB), // MediumPurple
        Color(0xFF3CB371), // MediumSeaGreen
        Color(0xFF800080), // Purple
        Color(0xFF00FF00), // Lime
        Color(0xFFFF69B4), // HotPink
        Color(0xFFADFF2F), // GreenYellow
        Color(0xFF7FFFD4), // Aquamarine
        Color(0xFF66CDAA), // MediumAquamarine
        Color(0xFFDA70D6), // Orchid
        Color(0xFF20B2AA), // LightSeaGreen
        Color(0xFF00CED1), // DarkTurquoise
        Color(0xFFFFDAB9), // PeachPuff
        Color(0xFFDAF7A6)  // Magic Mint
)





