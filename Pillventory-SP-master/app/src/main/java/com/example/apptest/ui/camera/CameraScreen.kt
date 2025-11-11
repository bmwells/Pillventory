package com.example.apptest.ui.camera


//import kotlin.coroutines.s

//import com.chaquo.python.PyObject
//import com.chaquo.python.Python
//import com.chaquo.python.android.AndroidPlatform
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.apptest.BottomNavigationBar
import com.example.apptest.PillTopAppBar
import com.example.apptest.R
import com.example.apptest.data.PillInfo
import com.example.apptest.ui.inventory.temp.StorageUtil
import com.example.apptest.ui.navigation.NavigationDestination
import com.example.pillsv.camera.CountResultViewModel
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// Ave
// Defines related information to the Camera page - route and title reference

object CameraDestination: NavigationDestination {
    override val route = "camera"
    override val titleRes = R.string.camera_title // Added to XML because this is type Int
}


// Ave
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    navController: NavHostController,
    navPillResults: (Int) -> Unit,
   // viewModel2: CameraSettingsViewModel = hiltViewModel()
) {
    Scaffold (
        topBar = { // Creates top bar with title
            PillTopAppBar(
                title = stringResource(CameraDestination.titleRes),
                canNavigateBack = false,
                navigateUp = {}
            )
        },
        bottomBar = { // Creates bottom navigation bar
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        CameraView(
            modifier = Modifier.padding(innerPadding),
            navPillResults = navPillResults,
       //     viewModel2 = viewModel2,
        )

        //PlaceholderScreen(navPillResults,Modifier.padding(innerPadding))

    }
}
// Needs to be above CameraView so it can see the function
fun writeNewRecord(
    pillId: String,
    userId: String,
    pillCount: String,
    date: String,
    description: String,
    tags: String,
    imageRef: String
) {
    val database = Firebase.database.reference

    val pill = PillInfo(
        pillId = pillId,
        count = pillCount,
        date = date,
        description = description,
        tags = tags,
        imageRef = imageRef
    )

    val userId = userId
//    val green = 255
//    val red = 255
//    val blue = 255
    database.child("users").child(userId).child(pillId).setValue(pill)
        .addOnSuccessListener {
            println("Added to the database")
        }
        .addOnFailureListener{
            println("ERROR not added to database")
        }
}

// Joe And Ave
// Creates view for camera screen with buttons for both take an picture or import
@Composable
private fun CameraView(
    modifier : Modifier,
    navPillResults: (Int) -> Unit,
    viewModel: CountResultViewModel = hiltViewModel()
) {

    // Camera settings related
    LaunchedEffect (viewModel) {
        viewModel.retrieveCameraSettings()
    }
    // retrieved colors if any
    val redAmt = viewModel.redAmt.collectAsState().value
    val greenAmt = viewModel.greenAmt.collectAsState().value
    val blueAmt = viewModel.blueAmt.collectAsState().value
    val numberBool = viewModel.isNumberCounting.collectAsState().value

    println("VALUES COLOR $redAmt, $greenAmt, $blueAmt, bool: $numberBool")

    val context = LocalContext.current

    //Retrieves User Information
    val userID = viewModel.getUserID()
    val todayDate = viewModel.getDate()
    val isGuest = viewModel.isUserGuest()
    val coroutineScope = CoroutineScope(Dispatchers.IO)

    var uri by remember {
        mutableStateOf<Uri?>(null)
    }
//    var new_uri by remember {
//        mutableStateOf<Uri?>(null)
//    }
    var bitmap by remember{
        mutableStateOf<Bitmap?>(null) }

    var cameraBitmap: Bitmap? by remember {
        mutableStateOf<Bitmap?>(null)
    }

    var cameraUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var procBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    var procCount by remember{
        mutableStateOf<Int>(0)
    }
    var finalBitmap by remember{
        mutableStateOf<Bitmap?>(null)
    }
    var imgUrl by remember{
        mutableStateOf("")
    }
    var imageTaken = false
    if (uri != null) {
        imageTaken = true
        println("value of image taken is : ${imageTaken}")
    }
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val lifecycleOwner = LocalLifecycleOwner.current
//    val context = LocalContext.current // creates the context for this activity
    //val userID = viewModel.getUserID()
    val singlePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {

            uri = it
            it?.let{ uri ->
                procCount = 0
                bitmap = uriToBitmap(context, uri)
                if(!Python.isStarted()){
                    Python.start(AndroidPlatform(context))
                }

                val py = Python.getInstance()
                val module = py.getModule("main")
                val preference = module["setPreference"]
                preference?.call(redAmt, greenAmt, blueAmt, numberBool)

                val temp = module.get("main")
                val imageString = bitmap?.let { it1 -> getImageString(it1) }
                val procString = temp?.call(imageString).toString()
                val tempBitmap = getBitmapFromString(procString)
                procBitmap = tempBitmap
                val counter =module.get("getCount")
                val count = counter?.call()?.toInt()
                if (count != null) {
                    procCount = count
                    procCount -= 1
                }
                Log.d("Zyn","$procCount")
                //procBitmap = bitmap?.let { it1 -> sendPython(context, it1,userID) }

            }
                if (uri != null) {
                    uri = procBitmap?.let { it1 -> getImageUri(context, it1) }
                    Log.d("joe", "$uri")
                }
        }

    )





    val preview = Preview.Builder().build() // builds the camera preview
    val previewView = remember {
        PreviewView(context)
    }
    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    val imageCapture = remember {
        ImageCapture.Builder().build()
    }
    var entry_description by remember { mutableStateOf("") }
    // Creates a coroutine scope for the camera use
    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview, imageCapture)
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }
    var showDialog by remember { mutableStateOf(false) }


    if (imageTaken == true) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            procBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "some useful description",
                    modifier = Modifier
                        .size(500.dp) // Increase the size of the image
                        .padding(bottom = 16.dp) // Add space between the image and the text
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (!isGuest) {
                Spacer(modifier = Modifier.width(100.dp)) // Add space between the buttons
                TextField(
                    value = entry_description,
                    onValueChange = { entry_description = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Pill Count = $procCount")

            Spacer(modifier = Modifier.weight(1f)) // Create flexible space above the buttons

            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = {
                        imageTaken = false
                        uri = null
                    }) {
                        Text(text = "Cancel")
                    }
                    if (!isGuest) {
                        Spacer(modifier = Modifier.width(100.dp)) // Add space between the buttons
                        Button(onClick = {
                            uri?.let {
                                StorageUtil.uploadToStorage(uri = it, context = context, type = "image") { imgUrl = it }
                                showDialog = true
                            }
                        }) {
                            Text("Upload")
                        }
//                        TextField(
//                            value = entry_description,
//                            onValueChange = { entry_description = it },
//                            label = { Text("Description") },
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(horizontal = 16.dp)
//                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Create flexible space below the buttons

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Confirmation") },
                    text = { Text("Do you want to save the uploaded image?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (imgUrl.isNotBlank()) {
                                    val pillId = UUID.randomUUID()

                                    writeNewRecord(
                                        pillId = pillId.toString(),
                                        userId = userID,
                                        pillCount = procCount.toString(),
                                        date = todayDate,
                                        description = entry_description,
                                        tags = "FILLER",
                                        imageRef = imgUrl
                                    )
                                }
                                showDialog = false
                                imageTaken = false
                                uri = null
                            }
                        ) {
                            Text("Save")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                showDialog = false
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
            Text("Upload Successful")
        }
    }
    
    else{
        Box(contentAlignment = Alignment.BottomCenter, modifier = modifier.fillMaxSize()) {
            AndroidView({ previewView }, modifier = Modifier.fillMaxSize())

            Row(modifier = Modifier.padding(30.dp)) {
                // Button for taking image
                IconButton(
                    onClick = {
                        captureImage(imageCapture, context) {
                            cameraUri = it
                            uriToBitmap(context, it)?.let { bitmap -> cameraBitmap = bitmap }
                            if (!Python.isStarted()) {
                                Python.start(AndroidPlatform(context))
                            }
                            val py = Python.getInstance()
                            val module = py.getModule("main")
                            val preference = module["setPreference"]
                            preference?.call(redAmt, greenAmt, blueAmt, numberBool)

                            val temp = module.get("main")
                            val imageString = cameraBitmap?.let { it1 -> getImageString(it1) }
                            val procString = temp?.call(imageString).toString()
                            val tempBitmap = getBitmapFromString(procString)
                            procBitmap = tempBitmap
                            uri = procBitmap?.let { it1 -> getImageUri(context, it1) }
                            Log.d("Rushil", "$uri")
                            val counter = module.get("getCount")
                            val count = counter?.call()?.toInt()
                            if (count != null) {
                                procCount = count
                                procCount -= 1
                            }
                        }

                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Black)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_camera_alt_24),
                        contentDescription = "Take Picture",
                        tint = Color.White
                    )

                }
                Spacer(modifier = Modifier.weight(1f))
                // Button for Gallery
                IconButton(
                    onClick = {
                        singlePhotoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },

                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.DarkGray)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_photo_library_24),
                        contentDescription = "Upload",
                        tint = Color.White
                    )

                }
                //procBitmap?.let { ResultScreen(bitmap = it) }
            }

        }
    }
}



// Ave
// Looking into ways to "extract" or use the image take
private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

// Joe and Ave
// Capture image from Camera
private fun captureImage(imageCapture: ImageCapture, context: Context, callback: (Uri) -> Unit) {

    // Need to provide a name for the image to save to

    val name = "CameraxImage.jpeg"
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, name)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg") // media store type is jpeg
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
        }
    }
    val outputOptions = ImageCapture.OutputFileOptions
        .Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, //Image reference
            contentValues
        )
        .build()
    imageCapture.takePicture( // Will store the image on the local media store (photos)
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {

                println("Successs")
                val msg =
                    "Photo capture success: ${outputFileResults.savedUri}" // Message shown on UI for image saved
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                outputFileResults.savedUri?.let { uri -> callback(uri) }
            }


            override fun onError(exception: ImageCaptureException) {
                println("Failed $exception")
            }
        }
    )

}

// Joe
// Converts Bitmap to Image String
// Needed to send image to image processing file
private fun getImageString(bitmap: Bitmap): String {
    val temp = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, temp)
    val imageBytes = temp.toByteArray()
    return android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT)
}
//Joe
// Converts Screen to Bitmap
private fun getBitmapFromString(encodedString: String): Bitmap? {
    val decodedBytes = android.util.Base64.decode(encodedString, android.util.Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
}
// Function to convert URI to Bitmap
private fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        null
    }
}

//???
fun getImageUri(inContext: Context, inImage: Bitmap): Uri {
    val bytes = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path =
        MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
    return Uri.parse(path)
}

//private fun sendPython(context: Context, bitmap: Bitmap, userID: String) : Bitmap? {
//    val database = Firebase.database.reference
//    val folderReference = database.child("current image").child(userID)
//
//    if(!Python.isStarted()){
//        Python.start(AndroidPlatform(context))
//    }
//    val py = Python.getInstance()
//    val module = py.getModule("main")
//    val temp = module.get("main")
//    val imageString = getImageString(bitmap)
//    val procString = temp?.call(imageString).toString()
//    val procBitmap = getBitmapFromString(procString)
//    val counter =module.get("getCount")
//    val count = counter?.call()?.toInt()
//    //val passCount = count.toString()
//    val processedInfo = count?.let {
//        if (procBitmap != null) {
//            ProcessedInfo(count = it, bitmap = procBitmap)
//        }
//    }
//
//    folderReference.setValue(processedInfo)
//    return procBitmap
//}

//@Composable
//fun ResultScreen(bitmap: Bitmap) {
//    Box(
//        contentAlignment = Alignment.Center,
//        modifier = Modifier.fillMaxSize()
//    ) {
//        Image(
//            bitmap = bitmap.asImageBitmap(),
//            contentDescription = "some useful description",
//            // You can add other modifiers here if needed
//        )
//    }
//}

//Joe
//private fun sendPython(context: Context, bitmap: Bitmap, userID: String) : Bitmap? {
//    //val database = Firebase.database.reference
//    //val folderReference = database.child("current image").child(userID)
//
//    if(!Python.isStarted()){
//        Python.start(AndroidPlatform(context))
//    }
//    val py = Python.getInstance()
//    val module = py.getModule("main")
//    val temp = module.get("main")
//    val imageString = getImageString(bitmap)
//    val procString = temp?.call(imageString).toString()
//    val procBitmap = getBitmapFromString(procString)
//    val counter =module.get("getCount")
//    val count = counter?.call()?.toInt()
//    //val passCount = count.toString()
//
//    return procBitmap
//}
@Composable
fun BitmapImage(bitmap: Bitmap) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize() // Ensure the image takes up the entire available space
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "some useful description",
            // You can add other modifiers here if needed
        )
    }
}

