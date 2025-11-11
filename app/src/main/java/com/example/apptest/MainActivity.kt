package com.example.apptest

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.chaquo.python.PyException
import com.example.apptest.ui.navigation.PillNavHost
import com.example.apptest.ui.theme.AppTestTheme
import dagger.hilt.android.AndroidEntryPoint
import org.opencv.android.OpenCVLoader


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Implement camera related code
                // Allow to go to Camera Screen
            } else {
                // Camera permission denied (Handle denied operation)
                // :( message
            }

        }
//Joe
private val writeExternalStoragePermissionRequest =
    registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            // Permission granted, you can now write to external storage
        } else {
            // Handle permission denied
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {

        // Handle splash screen transition
        installSplashScreen()

        super.onCreate(savedInstanceState)


        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA // Remember to import Manifest to access CAMERA
            ) -> {
                // Camera permission already granted
                // Implement camera related code
            }
            else -> {
                cameraPermissionRequest.launch(Manifest.permission.CAMERA)

            }
        }

        when(PackageManager.PERMISSION_GRANTED){
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE // Remember to import Manifest to access CAMERA
            ) -> {
                // Camera permission already granted
                // Implement camera related code
            }
            else -> {
                writeExternalStoragePermissionRequest.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)

            }
        }

//        when (PackageManager.PERMISSION_GRANTED){
//            ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ) == PackageManager.PERMISSION_GRANTED -> {
//                // Permission already granted, no need to do anything
//            }
//            else -> {
//                writeExternalStoragePermissionRequest.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            }
//        }



        setContent {
            AppTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ReadAndWrite().initializeDbRef()
                    val navController = rememberNavController()
                    PillNavHost(navController)
                }
            }
        }
    }
}


