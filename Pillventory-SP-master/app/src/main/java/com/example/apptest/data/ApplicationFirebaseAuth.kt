package com.example.apptest.data

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ApplicationFirebaseAuth: Application() {
    // Specified in the AndroidManifest to use Hilt on load
}