package com.example.apptest.ui.settings.camerasettings

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ComposeShader
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.RectF
import android.graphics.Shader
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toRect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.apptest.PillTopAppBar
import com.example.apptest.R
import com.example.apptest.ui.navigation.NavigationDestination
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import android.graphics.Color as AndroidColor


object CameraSettingsDestination: NavigationDestination {
    override val route = "camera_settings"
    override val titleRes = R.string.camera_settings_title
}


// Color Wheel composable
@Composable
fun HueBar(
    setColor: (Float) -> Unit
) {
    val scope = rememberCoroutineScope()
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val pressOffset = remember {
        mutableStateOf(Offset.Zero)
    }

    Canvas(
        modifier = Modifier
            .height(30.dp)
            .width(300.dp)
            .clip(RoundedCornerShape(50))
            .emitDragGesture(interactionSource)
    ) {
        val drawScopeSize = size
        val bitmap = Bitmap.createBitmap(size.width.toInt(), size.height.toInt(), Bitmap.Config.ARGB_8888)
        val hueCanvas = Canvas(bitmap)

        val huePanel = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())
        val hueColors = IntArray((huePanel.width()).toInt())
        var hue = 0f
        for (i in hueColors.indices) {
            hueColors[i] = AndroidColor.HSVToColor(floatArrayOf(hue, 1f, 1f))
            hue += 360f / hueColors.size
        }

        val linePaint = Paint()
        linePaint.strokeWidth = 0F
        for (i in hueColors.indices) {
            linePaint.color = hueColors[i]
            hueCanvas.drawLine(i.toFloat(), 0F, i.toFloat(), huePanel.bottom, linePaint)
        }

        drawBitmap(
            bitmap = bitmap,
            panel = huePanel
        )

        fun pointToHue(pointX: Float): Float {
            val width = huePanel.width()
            val x = when {
                pointX < huePanel.left -> 0F
                pointX > huePanel.right -> width
                else -> pointX - huePanel.left
            }
            return x * 360f / width
        }


        scope.collectForPress(interactionSource) { pressPosition ->
            val pressPos = pressPosition.x.coerceIn(0f..drawScopeSize.width)
            pressOffset.value = Offset(pressPos, 0f)
            val selectedHue = pointToHue(pressPos)
            setColor(selectedHue)

        }


        drawCircle(
            Color.White,
            radius = size.height/2,
            center = Offset(pressOffset.value.x, size.height/2),
            style = Stroke(
                width = 2.dp.toPx()
            )
        )

    }
}

fun CoroutineScope.collectForPress(
    interactionSource: InteractionSource,
    setOffset: (Offset) -> Unit
) {
    launch {
        interactionSource.interactions.collect { interaction ->
            (interaction as? PressInteraction.Press)
                ?.pressPosition
                ?.let(setOffset)
        }
    }
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
private fun Modifier.emitDragGesture(
    interactionSource: MutableInteractionSource
): Modifier = composed {
    val scope = rememberCoroutineScope()

    pointerInput(Unit) {
        detectDragGestures { input, _ ->
            scope.launch {
                interactionSource.emit(PressInteraction.Press(input.position))
            }
        }
    }.clickable(interactionSource, null) {

    }
}

private fun DrawScope.drawBitmap(
    bitmap: Bitmap,
    panel: RectF
) {
    drawIntoCanvas {
        it.nativeCanvas.drawBitmap(
            bitmap,
            null,
            panel.toRect(),
            null
        )
    }
}

@Composable
fun SatValPanel(
    hue: Float,
    setSatVal: (Float, Float) -> Unit
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    val scope = rememberCoroutineScope()
    var sat: Float
    var value: Float

    val pressOffset = remember {
        mutableStateOf(Offset.Zero)
    }

    Canvas(
        modifier = Modifier
            .size(200.dp)
            .emitDragGesture(interactionSource)
            .clip(RoundedCornerShape(12.dp))
    ) {
        val cornerRadius = 12.dp.toPx()
        val satValSize = size

        val bitmap = Bitmap.createBitmap(size.width.toInt(), size.height.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val satValPanel = RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat())

        val rgb = AndroidColor.HSVToColor(floatArrayOf(hue, 1f, 1f))

        val satShader =  LinearGradient(
            satValPanel.left, satValPanel.top, satValPanel.right, satValPanel.top,
            -0x1, rgb, Shader.TileMode.CLAMP
        )
        val valShader = LinearGradient(
            satValPanel.left, satValPanel.top, satValPanel.left, satValPanel.bottom,
            -0x1, -0x1000000, Shader.TileMode.CLAMP
        )

        canvas.drawRoundRect(
            satValPanel,
            cornerRadius,
            cornerRadius,
            Paint().apply {
                shader = ComposeShader(
                    valShader,
                    satShader,
                    PorterDuff.Mode.MULTIPLY
                )
            }
        )

        drawBitmap(
            bitmap = bitmap,
            panel = satValPanel
        )


        fun pointToSatVal(pointX: Float, pointY: Float): Pair<Float, Float> {
            val width = satValPanel.width()
            val height = satValPanel.height()

            val x = when {
                pointX < satValPanel.left -> 0f
                pointX > satValPanel.right -> width
                else -> pointX - satValPanel.left
            }

            val y = when {
                pointY < satValPanel.top -> 0f
                pointY > satValPanel.bottom -> height
                else -> pointY - satValPanel.top
            }

            val satPoint = 1f / width * x
            val valuePoint = 1f - 1f / height * y

            return satPoint to valuePoint
        }

        scope.collectForPress(interactionSource) { pressPosition ->
            val pressPositionOffset = Offset(
                pressPosition.x.coerceIn(0f..satValSize.width),
                pressPosition.y.coerceIn(0f..satValSize.height)
            )


            pressOffset.value = pressPositionOffset
            val (satPoint, valuePoint) = pointToSatVal(pressPositionOffset.x, pressPositionOffset.y)
            sat = satPoint
            value = valuePoint

            setSatVal(sat, value)
        }

        drawCircle(
            color = Color.White,
            radius = 8.dp.toPx(),
            center = pressOffset.value,
            style = Stroke(
                width = 2.dp.toPx()
            )
        )

        drawCircle(
            color = Color.White,
            radius = 2.dp.toPx(),
            center = pressOffset.value,
        )


    }
}


// Checkbox composable
@Composable
fun SwitchWithText(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector? = null
) {
    Row {
        Text(text = text, modifier = Modifier.offset((-16).dp, (10).dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier
                .offset(24.dp, 0.dp)
                .semantics { contentDescription = text },
            thumbContent = {
                if (icon != null && checked) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(SwitchDefaults.IconSize)
                    )
                }
            }
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraSettingsScreen(
    navController: NavHostController,
    viewModel: CameraSettingsViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            PillTopAppBar(
                title = stringResource(id = CameraSettingsDestination.titleRes),
                navigateUp = { navController.popBackStack() },
                canNavigateBack = true,
            )
        }
    ) { innerPadding ->
        CameraSettingsBody(
            modifier = Modifier.padding(innerPadding),
            navController = navController, // Pass the navController instance
            viewModel = viewModel
        )
    }
}

// Functions for RGB to HSV transitions
fun rgbToHSV(red: Int, green: Int, blue: Int): FloatArray {
    val hsv = FloatArray(3)
    val rgb = floatArrayOf(red / 255f, green / 255f, blue / 255f)
    val max = rgb.maxOrNull() ?: 0f
    val min = rgb.minOrNull() ?: 0f
    val delta = max - min

    // Calculate hue
    hsv[0] = if (delta == 0f) 0f
    else {
        when (max) {
            rgb[0] -> 60 * (((rgb[1] - rgb[2]) / delta) % 6)
            rgb[1] -> 60 * (((rgb[2] - rgb[0]) / delta) + 2)
            else -> 60 * (((rgb[0] - rgb[1]) / delta) + 4)
        }
    }

    // Calculate saturation
    hsv[1] = if (max == 0f) 0f else delta / max

    // Calculate value
    hsv[2] = max

    return hsv
}
fun hsvToRGB(hue: Float, saturation: Float, value: Float): IntArray {
    val rgb = IntArray(3)

    val c = value * saturation
    val x = c * (1 - Math.abs(((hue / 60) % 2) - 1))
    val m = value - c

    val (r1, g1, b1) = when {
        hue < 60 -> Triple(c, x, 0f)
        hue < 120 -> Triple(x, c, 0f)
        hue < 180 -> Triple(0f, c, x)
        hue < 240 -> Triple(0f, x, c)
        hue < 300 -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }

    rgb[0] = ((r1 + m) * 255).toInt()
    rgb[1] = ((g1 + m) * 255).toInt()
    rgb[2] = ((b1 + m) * 255).toInt()

    return rgb
}


@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun CameraSettingsBody(
    modifier: Modifier,
    navController: NavHostController,
    viewModel: CameraSettingsViewModel
) {

    val context = LocalContext.current

    val isGuest = viewModel.isUserGuest()


    // Get user preferences
    LaunchedEffect(viewModel) {
        try {
            viewModel.fetchUserPref()
        } catch (e: Exception) {
            // Handle the exception, e.g., log or show a toast message
            Toast.makeText(context, "Error fetching preferences: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Get values from viewmodel
    val red = viewModel.getRed()
    val green = viewModel.getGreen()
    val blue = viewModel.getBlue()
    val check = viewModel.getCheck()

    // Convert rgb to hsv
    val hsv = rgbToHSV(red, green, blue)

    // MutableState for color wheel and switch state
    var hue by remember { mutableFloatStateOf(hsv[0]) }
    var sat by remember { mutableFloatStateOf(hsv[1]) }
    var value by remember { mutableFloatStateOf(hsv[2]) }
    var switchChecked by remember { mutableStateOf(check) }

    // Convert hsv to rgb
    val newRGB = hsvToRGB(hue, sat, value)

    // Update UI when user preferences are fetched
    LaunchedEffect(red, green, blue, check) {
        hue = hsv[0]
        sat = hsv[1]
        value = hsv[2]
        switchChecked = check
    }


    Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Text display
            Text(
                text = stringResource(id = R.string.select_color),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                textAlign = TextAlign.Center
            )

        // Color Wheel
        SatValPanel(hue = hue) { newSat, newValue ->
            sat = newSat
            value = newValue
        }

        Spacer(modifier = Modifier.height(8.dp))

        HueBar { newHue ->
            hue = newHue
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(50.dp)
                .background(Color.hsv(hue, sat, value))
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Check box
        SwitchWithText(
            text = stringResource(R.string.num_indicator),
            checked = switchChecked,
            onCheckedChange = { newChecked ->
                switchChecked = newChecked
            },
            icon = Icons.Filled.Check
        )

        Spacer(modifier = Modifier.height(64.dp))

            // Save Changes Button
            Button(
                onClick = {
                    // Update user pref in firebase
                    viewModel.updateUserPref(newRGB[0], newRGB[1], newRGB[2], switchChecked)
                    Toast.makeText(context, "Save Successful", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                          },
                colors = ButtonDefaults.buttonColors(Color(0xFF447903)),
                modifier = Modifier
                    .width(250.dp)
                    .padding(vertical = 8.dp)
            ) {
                Text(text = stringResource(id = R.string.save_changes))
            }

            // Discard Changes Button
            Button(
                onClick = {
                    // Update user pref to original user pref settings
                    viewModel.updateUserPref(red, green, blue, check)
                    Toast.makeText(context, "Changes Discarded", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                          },
                colors = ButtonDefaults.buttonColors(Color.Red),
                modifier = Modifier
                    .width(250.dp)
                    .padding(vertical = 8.dp)
            ) {
                Text(text = stringResource(id = R.string.discard_changes))
            }

            // Reset Changes Button
            Button(
                onClick = {
                    // Update user pref to factory settings
                    viewModel.resetUserPref()
                    Toast.makeText(context, "Preferences Reset", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                modifier = Modifier
                    .width(250.dp)
                    .padding(vertical = 8.dp)
            ) {
                Text(text = stringResource(id = R.string.reset_pref))
            }


        }

}






